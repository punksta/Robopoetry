package com.punksta.apps.robopoetry.service

import android.util.Log
import com.punksta.apps.robopoetry.BuildConfig
import com.punksta.apps.robopoetry.model.TaskTextProvider
import com.punksta.apps.robopoetry.model.getTaskTextProvider
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import com.punksta.apps.robopoetry.service.util.OnSpeechListener

/**
 * Created by stanislav on 11/12/17.
 */
class YandexSpeakService : BaseYandexSpeechService() {
    override val apiKey: String = BuildConfig.yandexApiKey

    private lateinit var provider: TaskTextProvider

    override fun getTextForTask(task: SpeechTask): String {
        return provider.provide(task)
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


    }
}