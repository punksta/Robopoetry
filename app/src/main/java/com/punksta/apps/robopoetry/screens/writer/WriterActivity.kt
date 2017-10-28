package com.punksta.apps.robopoetry.screens.writer

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.*
import com.punksta.apps.robopoetry.ext.setTypeFace
import com.punksta.apps.robopoetry.ext.textChangesEvents
import com.punksta.apps.robopoetry.model.Robot
import com.punksta.apps.robopoetry.model.Voice
import com.punksta.apps.robopoetry.model.getModel
import com.punksta.apps.robopoetry.screens.common.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.yandex.speechkit.Vocalizer


/**
 * Created by stanislav on 1/2/17.
 */
class WriterActivity : AppCompatActivity(), (EntityItem) -> Unit {

    private var load: Disposable? = null
    private var update: Disposable? = null
    private var callback: Disposable? = null

    private val brodcast = PlayingBroadcastReceiver()

    private val button: Button
        get() = findViewById(R.id.stop_button)

    private val writer: WriterInfo?
        get() = intent.getParcelableExtra<WriterInfo>("writer")

    private val celebration: Celebration?
        get() = intent.getParcelableExtra("celebration")

    private val speckers: SpeackersView
        get() = findViewById(R.id.speakers)


    fun getCurrentVoice() : String{
        return (getModel().getCurrent().voice as? Voice.YandexVoice?)?.voice?:Vocalizer.Voice.ALYSS
    }

    override fun invoke(p1: EntityItem) {
        val voice : String = getCurrentVoice()
        when (p1) {
            is Poem -> {
                startService(Intent(this, PlayingService::class.java)
                        .setAction(ACTION_PLAY)
                        .putExtra(EXTRA_VOICE, voice)
                        .putExtra(EXTRA_WRITER, writer)
                        .putExtra(EXTRA_POEM, getModel().getPoem(writer!!.id, p1.id).blockingGet())
                )
            }
            is CelebrationItem -> {
                startService(Intent(this, PlayingService::class.java)
                        .setAction(ACTION_PLAY)
                        .putExtra(EXTRA_VOICE, voice)
                        .putExtra(EXTRA_CELEBRATION, celebration)
                        .putExtra(EXTRA_CELEBRATION_ITEM, p1)
                        .putExtra(EXTRA_USER_NAME, (findViewById<TextView>(R.id.filter_by_name)).text.toString())
                )
            }
        }
    }


    private var loaded = false

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_writer)

        overridePendingTransition(R.anim.left_in, R.anim.left_out);


        (findViewById<TextView>(R.id.filter_by_name)).setTypeFace("clacon.ttf")

        (findViewById<RecyclerView>(R.id.poems_items)).layoutManager = LinearLayoutManager(this)

        val s = speckers

        s.listener = {
            getModel().setCurrent(it)
            s.setActive(it)
            notifyRobotChange(it);
        }

        s.showRobots(getModel().getRobots(), getModel().getCurrent(), Picasso.with(this))
        loaded = false

        button.setOnClickListener {
            startService(Intent(this, PlayingService::class.java)
                    .setAction(ACTION_STOP)
            )
        }
    }

    fun notifyRobotChange (robot: Robot) {
        speckers.clearSpeacking()

        startService(Intent(this, PlayingService::class.java)
                .setAction(ACTION_PLAY)
                .putExtra(EXTRA_VOICE, getCurrentVoice())
                .putExtra(EXTRA_ROBOT_GREETING, getModel().getGreetingForRobot(robot))
                .putExtra(EXTRA_ROBOT_NAME, getString(robot.nameId))
        )
    }


    override fun onResume() {
        super.onResume()
        registerReceiver(brodcast, IntentFilter(PlayingBroadcastReceiver.BRODCAST_ACTION))

        callback = brodcast.observe()
                .subscribe { event ->
                    when (event) {
                        is Event.PlayingStartted -> {
                            button.isEnabled = true
                            speckers.setSpeacking(getModel().getCurrent())
                        }
                        else -> {
                            button.isEnabled = false
                            speckers.clearSpeacking()
                        }
                    }
                }


        startService(Intent(this, PlayingService::class.java)
                .setAction(ACTION_REGISTER)
        )

        val w = writer
        val c = celebration
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
                }
                update = (findViewById<TextView>(R.id.filter_by_name)).textChangesEvents(false)
                        .flatMap { getModel().queryPoems(writerId = w.id, query = it, cutLimit = 40).toObservable() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { list ->
                            ((findViewById<RecyclerView>(R.id.poems_items)).adapter as PoemAdapter).update(list)
                        }
            }
            c != null -> {
                (findViewById<TextView>(R.id.filter_by_name)).setHint(R.string.name)

                getModel().getCelebration(c)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { list ->
                            (findViewById<RecyclerView>(R.id.poems_items)).adapter = PoemAdapter(list.toMutableList(), this)
                        }
            }
            else -> {
                finish()
            }
        }

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(brodcast)
        callback?.dispose()
        load?.dispose()
        update?.dispose()
    }

    companion object {
        fun getIntent(activity: AppCompatActivity, writerInfo: WriterInfo): Intent {
            return Intent(activity, WriterActivity::class.java)
                    .putExtra("writer", writerInfo)
        }

        fun getIntent(activity: AppCompatActivity, celebration: Celebration): Intent {
            return Intent(activity, WriterActivity::class.java)
                    .putExtra("celebration", celebration)
        }
    }
}