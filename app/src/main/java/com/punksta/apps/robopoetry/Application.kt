package com.punksta.apps.robopoetry

import android.support.multidex.MultiDexApplication
import com.punksta.apps.robopoetry.entity.GreetingsSpeechTask
import com.punksta.apps.robopoetry.entity.PoemSpeechTask
import com.punksta.apps.robopoetry.model.GreetingsSpeechTaskProvider
import com.punksta.apps.robopoetry.model.PoemReadProvider
import com.punksta.apps.robopoetry.model.TaskTextProviderImpl

/**
 * Created by stanislav on 1/6/17.
 */
class Application : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        TaskTextProviderImpl.registerProvide(GreetingsSpeechTask::class.java, GreetingsSpeechTaskProvider())
        TaskTextProviderImpl.registerProvide(PoemSpeechTask::class.java, PoemReadProvider(this))
    }
}
