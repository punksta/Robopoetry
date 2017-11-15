package com.punksta.apps.robopoetry.model

import com.punksta.apps.robopoetry.service.SpeechTask

/**
 * Created by stanislav on 11/15/17.
 */

interface TaskTextProviderT<in T : SpeechTask> {
    public fun provide(t: T): String
}

private data class ClassAndProvider<T : SpeechTask>(val clazz: Class<T>, val provider: TaskTextProviderT<T>)


object TaskTextProviderImpl : TaskTextProvider {
    private val providers: MutableList<ClassAndProvider<*>> = mutableListOf()

    override fun <T : SpeechTask> provide(speechTask: T): String {
        return providers.first { it.clazz == speechTask.javaClass }
                .let { it as ClassAndProvider<T> }
                .let { it.provider.provide(speechTask) }

    }

    public fun <T : SpeechTask> registerProvide(clazz: Class<T>, provider: TaskTextProviderT<T>) {
        providers.add(ClassAndProvider(clazz, provider))
    }
}