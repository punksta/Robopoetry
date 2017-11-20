package com.punksta.apps.robopoetry.screens.writer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.*
import com.punksta.apps.robopoetry.ext.hidekeyKoard
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
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_writer.*
import java.util.concurrent.TimeUnit

/**
 * Created by stanislav on 1/2/17.
 */
class WriterActivity : AppCompatActivity(), (EntityItem) -> Unit {
    private var update: Disposable? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var pendingSavedState: Parcelable? = null;


    private val writer: WriterInfo?
        get() = intent.getParcelableExtra("writer")


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

            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_writer)

        poems_items.layoutManager = LinearLayoutManager(this)

        if (savedInstanceState == null) {
            overridePendingTransition(R.anim.left_in, R.anim.left_out)
        } else {
            pendingSavedState = savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY)
        }

        speakers.listener = {
            getModel().setCurrent(it)
            notifyRobotChange(it)
        }

        speakers.showRobots(getModel().getRobots(), getModel().getCurrent(), Picasso.with(this))

        stop_button.setOnClickListener {
            bindler?.stopLastTask()
        }

        poems_items.setOnTouchListener({ _, _ ->
            hidekeyKoard()
            false
        })
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
        speakers.clearSpeacking()

        val greeting = getModel().getGreetingForRobot(robot)

        sendTaskToService(GreetingsSpeechTask(greeting, robot.voice, getString(robot.nameId)))
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

    private val LAYOUT_MANAGER_KEY = "layout_manager";

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        (poems_items?.layoutManager as? LinearLayoutManager)
                ?.let {
                    outState?.putParcelable(LAYOUT_MANAGER_KEY, it.onSaveInstanceState())
                }
    }


    override fun onStart() {
        super.onStart()

        bindService(Intent(this, YandexSpeakService::class.java), serverConnection, Context.BIND_AUTO_CREATE)

        val w = writer

        when {
            w != null -> {
                filter_by_name.setHint(R.string.search)

                val isLoaded = poems_items.childCount > 0;

                update = filter_by_name.textChangesEvents(false)
                        .debounce(500, TimeUnit.MILLISECONDS)
                        .observeOn(Schedulers.io())
                        .mergeWith(Observable.just("").filter {
                            !isLoaded
                        })
                        .flatMap { getModel().queryPoems(writerId = w.id, query = it, cutLimit = 40).toObservable() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { list ->
                            val adapter = (poems_items.adapter)
                            when (adapter) {
                                null -> {
                                    poems_items.adapter = PoemAdapter(list.toMutableList(), this)
                                    pendingSavedState?.let((poems_items.layoutManager as LinearLayoutManager)::onRestoreInstanceState)
                                    pendingSavedState = null
                                }
                                else -> {
                                    (adapter as PoemAdapter).update(list)
                                }
                            }
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

    override fun onResume() {
        super.onResume()
        eventsSubject
                .distinctUntilChanged()
                .subscribe {
                    when (it) {
                        is SpeechEvent.OnProcessingStart -> {
                            speakers.clearSpeacking()
                            stop_button.isEnabled = true
                        }

                        is SpeechEvent.OnSpeechStart -> {
                            val robot = it.task.voice.toRobotEnum().robot
                            speakers.setActive(robot)
                            speakers.setSpeacking(robot)
                        }

                        else -> {
                            speakers.clearSpeacking()
                            stop_button.isEnabled = false
                        }
                    }
                }
                .also {
                    compositeDisposable.add(it)
                }


    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
        speakers.clearSpeacking()
        stop_button.isEnabled = false
    }

    override fun onStop() {
        super.onStop()
        unbindService(serverConnection)
        bindler?.removeListener(listener)
        update?.dispose()

    }

    companion object {
        fun getIntent(activity: AppCompatActivity, writerInfo: WriterInfo): Intent {
            return Intent(activity, WriterActivity::class.java)
                    .putExtra("writer", writerInfo)
        }
    }
}