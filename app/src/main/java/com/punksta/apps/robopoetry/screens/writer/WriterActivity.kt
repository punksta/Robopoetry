package com.punksta.apps.robopoetry.screens.writer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.*
import com.punksta.apps.robopoetry.ext.textChangesEvents
import com.punksta.apps.robopoetry.model.Robot
import com.punksta.apps.robopoetry.model.getModel
import com.punksta.apps.robopoetry.model.toRobotEnum
import com.punksta.apps.robopoetry.service.BaseYandexSpeechService
import com.punksta.apps.robopoetry.service.BaseYandexSpeechService.Companion.putTask
import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.YandexSpeakService
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import com.punksta.apps.robopoetry.service.util.OnSpeechListener
import com.punksta.apps.robopoetry.view.SpeackersView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

/**
 * Created by stanislav on 1/2/17.
 */
class WriterActivity : AppCompatActivity(), (EntityItem) -> Unit {

    private var load: Disposable? = null
    private var update: Disposable? = null
    private var callback: Disposable? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()


    private val button: Button
        get() = findViewById(R.id.stop_button)

    private val writer: WriterInfo?
        get() = intent.getParcelableExtra("writer")

    private val speckers: SpeackersView
        get() = findViewById(R.id.speakers)


    override fun invoke(p1: EntityItem) {
        when (p1) {
            is Poem -> {
                val task = PoemSpeechTask(
                        writer!!.id,
                        p1.id,
                        getModel().getCurrent().voice,
                        "${writer!!.name} - ${p1.name}"
                )
                sendTaskToService(task)

//                bindler?.playTask(task)
            }
        }
    }


    private var loaded = false

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_writer)

        overridePendingTransition(R.anim.left_in, R.anim.left_out)

        (findViewById<RecyclerView>(R.id.poems_items)).layoutManager = LinearLayoutManager(this)

        val s = speckers

        s.listener = {
            getModel().setCurrent(it)
            notifyRobotChange(it)
        }

        if (savedInstanceState == null) {
            s.showRobots(getModel().getRobots(), getModel().getCurrent(), Picasso.with(this))
            loaded = false
        }

        button.setOnClickListener {
            bindler?.stopLastTask()
        }
    }


    private fun sendTaskToService(task: SpeechTask) {

        val intent = Intent(this, YandexSpeakService::class.java).putTask(task)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun notifyRobotChange(robot: Robot) {
        speckers.clearSpeacking()

        val greeting = getModel().getGreetingForRobot(robot)

        sendTaskToService(GreetingsSpeechTask(greeting, robot.voice, getString(robot.nameId)))

//        bindler?.playTask()
//        startService(Intent(this, PlayingService::class.java)
//                .setAction(ACTION_PLAY)
//                .putExtra(EXTRA_VOICE, getCurrentVoice())
//                .putExtra(EXTRA_ROBOT_GREETING, getModel().getGreetingForRobot(robot))
//                .putExtra(EXTRA_ROBOT_NAME, getString(robot.nameId))
//        )
    }


    private var bindler: BaseYandexSpeechService.YandexSpeechBinder? = null
    private val eventsSubject: Subject<SpeechEvent<*>> = BehaviorSubject.create()

    private val listener = object : OnSpeechListener {
        override fun onEvent(speechEvent: SpeechEvent<*>) {
            eventsSubject.onNext(speechEvent)
        }
    }


    private val serverConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            this@WriterActivity.bindler?.removeListener(listener)
            this@WriterActivity.bindler = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service?.let {
                (service as? BaseYandexSpeechService.YandexSpeechBinder)?.let {
                    this@WriterActivity.bindler = it
                    it.addListener(listener, true)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        bindService(Intent(this, YandexSpeakService::class.java), serverConnection, Context.BIND_AUTO_CREATE)
    }


    override fun onResume() {
        super.onResume()

        val w = writer

        eventsSubject
                .distinctUntilChanged()
                .subscribe {
                    when (it) {
                        is SpeechEvent.OnProcessingStart -> {
                            speckers.clearSpeacking()
                            button.isEnabled = true
                        }

                        is SpeechEvent.OnSpeechStart -> {
                            val robot = it.task.voice.toRobotEnum().robot
                            speckers.setActive(robot)
                            speckers.setSpeacking(robot)
                        }

                        else -> {
                            speckers.clearSpeacking()
                            button.isEnabled = false
                        }
                    }
                }
                .also {
                    compositeDisposable.add(it)
                }


        when {
            w != null -> {
                (findViewById<TextView>(R.id.filter_by_name)).setHint(R.string.search)

                if (loaded.not()) {
                    load = getModel().queryPoems(writerId = w.id, cutLimit = 40)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { list ->
                                (findViewById<RecyclerView>(R.id.poems_items)).adapter = PoemAdapter(list.toMutableList(), this)
                            }
                            .also {
                                compositeDisposable.add(it)
                            }
                }
                update = (findViewById<TextView>(R.id.filter_by_name)).textChangesEvents(false)
                        .flatMap { getModel().queryPoems(writerId = w.id, query = it, cutLimit = 40).toObservable() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { list ->
                            ((findViewById<RecyclerView>(R.id.poems_items)).adapter as PoemAdapter).update(list)
                        }
                        .also {
                            compositeDisposable.add(it)
                        }
            }

            else -> {
                finish()
            }
        }

    }

    override fun onStop() {
        super.onStop()

        unbindService(serverConnection)
        bindler?.removeListener(listener)

        callback?.dispose()
        load?.dispose()
        update?.dispose()

        speckers.clearSpeacking()
        button.isEnabled = false

    }

    companion object {
        fun getIntent(activity: AppCompatActivity, writerInfo: WriterInfo): Intent {
            return Intent(activity, WriterActivity::class.java)
                    .putExtra("writer", writerInfo)
        }
    }
}