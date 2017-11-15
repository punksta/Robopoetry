package com.punksta.apps.robopoetry.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import com.punksta.apps.robopoetry.service.entities.YandexVoice
import com.punksta.apps.robopoetry.service.util.ClosableOnSpeechListener
import com.punksta.apps.robopoetry.service.util.OnSpeechListener
import com.punksta.apps.robopoetry.service.util.TaskEventListener
import ru.yandex.speechkit.SpeechKit
import ru.yandex.speechkit.Vocalizer

interface SpeechTask : Parcelable {
    val voice: YandexVoice
    val title: String
}


abstract class BaseYandexSpeechService : Service() {
    abstract val apiKey: String
    private val listeners: MutableSet<OnSpeechListener> = HashSet()
    private var lastEvent: SpeechEvent? = null
    private var lastTask: SpeechTask? = null
    private var lastVocalizer: Vocalizer? = null;


    abstract protected fun getTextForTask(task: SpeechTask): String


    inner open class YandexSpeechBinder : Binder() {
        protected inline val service: BaseYandexSpeechService
            get() = this@BaseYandexSpeechService

        public fun playTask(task: SpeechTask) = service.playTask(task)
        public fun stopLastTask() = service.stopLastTask()


        private val lastEvent: SpeechEvent?
            get() = service.lastEvent

        private val lastTask: SpeechTask?
            get() = service.lastTask


        fun removeListener(listener: OnSpeechListener) {
            service.removeListener(listener)
        }

        fun addListener(listener: OnSpeechListener, emmitCurrentState: Boolean) {
            service.addListener(listener, emmitCurrentState)
        }
    }


    protected fun createBinder(): YandexSpeechBinder {
        return YandexSpeechBinder()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return createBinder();
    }


    override fun onCreate() {
        super.onCreate()
        SpeechKit.getInstance().configure(this, apiKey)
    }


    private fun notifyListeners(event: SpeechEvent) {
        listeners.forEach { it.onEvent(event) }
    }

    private fun onNewEvent(event: SpeechEvent) {
        lastEvent = event;
        notifyListeners(event)
    }

    private fun setAndStartTask(task: SpeechTask) {
        cancelLastTask()

        val text = getTextForTask(task)

        val sender: (SpeechEvent) -> Unit = { event: SpeechEvent ->
            if (lastTask == task) {
                onNewEvent(event)
            }
        }

        lastEvent = null
        lastTask = task
        lastVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, task.voice.yandexVoiceId)
                .apply {
                    setListener(TaskEventListener(task, sender))
                    start()
                }

    }

    private fun cancelLastTask() {
        try {
            lastVocalizer?.setListener(null)
            lastVocalizer?.cancel()
        } catch (e: Throwable) {

        }
        lastTask = null
        lastEvent = null
        lastVocalizer = null
    }


    fun playTask(task: SpeechTask) {
        setAndStartTask(task)
    }

    fun stopLastTask() {
        val task = lastTask
        cancelLastTask()
        task?.run { onNewEvent(SpeechEvent.OnSpeechStopped(this)) }
    }

    fun addListener(listener: OnSpeechListener, emmitCurrentState: Boolean) {
        listeners.add(listener)
        if (emmitCurrentState) {
            val e = lastEvent;
            if (e != null) {
                listener.onEvent(e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listeners.forEach { removeListener(it) }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            STOP_TASK -> {
                stopLastTask()
                START_STICKY

            }
            PLAY_TASK -> {
                val task = intent.getParcelableExtra<SpeechTask>(PLAY_TASK)
                playTask(task)
                START_STICKY
            }
            else -> {
                START_NOT_STICKY
            }
        }
    }

    fun removeListener(listener: OnSpeechListener) {
        listeners.remove(listener)
        if (listener is ClosableOnSpeechListener) {
            try {
                listener.release()
            } catch (e: Throwable) {

            }
        }
    }

    companion object {
        private val TASK_KEY = "task"
        private val PLAY_TASK = "play_task"
        private val STOP_TASK = "stop_task"


        public fun Intent.putTask(task: SpeechTask) =
                apply {
                    action = PLAY_TASK
                    putExtra(TASK_KEY, task)
                }

        public fun Intent.putStopSpeach() =
                apply {
                    action = STOP_TASK
                }
    }

}