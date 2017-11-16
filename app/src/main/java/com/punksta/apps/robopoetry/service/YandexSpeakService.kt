package com.punksta.apps.robopoetry.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.punksta.apps.robopoetry.BuildConfig
import com.punksta.apps.robopoetry.model.TaskTextProvider
import com.punksta.apps.robopoetry.model.getTaskTextProvider
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import com.punksta.apps.robopoetry.service.util.NotificationSpeechListener
import com.punksta.apps.robopoetry.service.util.OnSpeechListener
import com.punksta.apps.robopoetry.service.util.PlayerListener

/**
 * Created by stanislav on 11/12/17.
 */
class YandexSpeakService : BaseYandexSpeechService() {
    override val apiKey: String = BuildConfig.yandexApiKey

    private lateinit var provider: TaskTextProvider

    override fun getTextForTask(task: SpeechTask): String {
        return provider.provide(task)
    }

    private val pendingIntentFactory: (SpeechTask) -> PendingIntent = { _ ->
        val intent = Intent(this, YandexSpeakService::class.java).putStopSpeach()
        PendingIntent.getService(
                this.applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate() {
        super.onCreate()

        provider = getTaskTextProvider()

        if (BuildConfig.DEBUG) {
            val loggerListener = object : OnSpeechListener {
                override fun onEvent(speechEvent: SpeechEvent) {
                    Log.v("YandexSpeakService", speechEvent.toString())
                }
            }

            addListener(loggerListener, true)
        }


        addListener(PlayerListener(this, "music/sound1.mp3"), true)
        addListener(NotificationSpeechListener(this, pendingIntentFactory), true)
    }
}