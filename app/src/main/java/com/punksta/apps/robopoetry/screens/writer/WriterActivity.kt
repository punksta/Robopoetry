package com.punksta.apps.robopoetry.screens.writer

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo
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
class WriterActivity : AppCompatActivity(), (Poem) -> Unit {

    private var load: Disposable? = null
    private var update: Disposable? = null
    private var callback: Disposable? = null

    private val brodcast = PlayingBroadcastReceiver()

    private val button: Button
        get() = findViewById(R.id.stop_button) as Button

    private val writer: WriterInfo
        get() = intent.getParcelableExtra<WriterInfo>("writer")

    private val speckers: SpeackersView
        get() = findViewById(R.id.speakers) as SpeackersView


    fun getCurrentVoice() : String{
        return (getModel().getCurrent().voice as? Voice.YandexVoice?)?.voice?:Vocalizer.Voice.ALYSS
    }

    override fun invoke(p1: Poem) {
        val voice : String = getCurrentVoice()

        startService(Intent(this, PlayingService::class.java)
                .setAction(ACTION_PLAY)
                .putExtra(EXTRA_VOICE, voice)
                .putExtra(EXTRA_WRITER, writer)
                .putExtra(EXTRA_POEM, getModel().getPoem(writer.id, p1.id).blockingGet())
        )
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


        (findViewById(R.id.filter_by_name) as TextView).setTypeFace("clacon.ttf")
        (findViewById(R.id.poems_items) as RecyclerView).layoutManager = LinearLayoutManager(this)

        val s = speckers

        s.listener = {
            getModel().setCurrent(it)
            s.setActive(it)
        }

        s.showRobots(getModel().getRobots(), getModel().getCurrent(), Picasso.with(this))
        loaded = false

        button.setOnClickListener {
            startService(Intent(this, PlayingService::class.java)
                    .setAction(ACTION_STOP)
            )
        }
    }


    override fun onResume() {
        super.onResume()
        registerReceiver(brodcast, IntentFilter(PlayingBroadcastReceiver.BRODCAST_ACTION))

        callback = brodcast.observe()
                .subscribe { event ->
                    when (event) {
                        is Event.PlayingStartted -> {
                            button.isEnabled = true
                        }
                        else -> {
                            button.isEnabled = false
                        }
                    }
                }


        startService(Intent(this, PlayingService::class.java)
                .setAction(ACTION_REGISTER)
        )

        if (loaded.not()) {
            load = getModel().queryPoems(writerId = writer.id, cutSize = 40)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { list ->
                        (findViewById(R.id.poems_items) as RecyclerView).adapter = PoemAdapter(list.toMutableList(), this)
                    }
        }
        update = (findViewById(R.id.filter_by_name) as TextView).textChangesEvents(false)
                .flatMap { getModel().queryPoems(writerId = writer.id, query = it, cutSize = 40).toObservable() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    ((findViewById(R.id.poems_items) as RecyclerView).adapter as PoemAdapter).update(list)
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
    }
}