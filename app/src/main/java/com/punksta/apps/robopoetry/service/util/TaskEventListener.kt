package com.punksta.apps.robopoetry.service.util

import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import ru.yandex.speechkit.Error
import ru.yandex.speechkit.Synthesis
import ru.yandex.speechkit.Vocalizer
import ru.yandex.speechkit.VocalizerListener


class TaskEventListener(private val task: SpeechTask,
                        private val eventSender: (SpeechEvent<*>) -> Unit) :
        VocalizerListener {

    private var lastEvent: SpeechEvent<*>? = null

    private val unit = Unit

    private val eventProxy: (SpeechEvent<*>) -> Unit = { event ->
        synchronized(unit, {
            if (lastEvent != event) {
                eventSender(event)
                lastEvent = event
            }
        })
    }

    override fun onSynthesisBegin(p0: Vocalizer?) {
        eventProxy(SpeechEvent.OnProcessingStart(task))
    }

    override fun onPlayingBegin(p0: Vocalizer?) {
        eventProxy(SpeechEvent.OnSpeechStart(task))
    }

    override fun onVocalizerError(p0: Vocalizer?, p1: Error) {
        eventProxy(SpeechEvent.OnSpeechError(task, p1))
    }

    override fun onSynthesisDone(p0: Vocalizer?, p1: Synthesis?) {

    }

    override fun onPlayingDone(p0: Vocalizer?) {
        eventProxy(SpeechEvent.OnSpeechEnd(task))
    }

}