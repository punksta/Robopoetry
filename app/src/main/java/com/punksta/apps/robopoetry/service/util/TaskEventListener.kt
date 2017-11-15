package com.punksta.apps.robopoetry.service.util

import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import ru.yandex.speechkit.Error
import ru.yandex.speechkit.Synthesis
import ru.yandex.speechkit.Vocalizer
import ru.yandex.speechkit.VocalizerListener

class TaskEventListener(private val task: SpeechTask,
                        private val eventSender: (SpeechEvent) -> Unit) :
        VocalizerListener {

    override fun onSynthesisBegin(p0: Vocalizer?) {
        eventSender(SpeechEvent.OnProcessingStart(task))
    }

    override fun onPlayingBegin(p0: Vocalizer?) {
        eventSender(SpeechEvent.OnSpeechStart(task))
    }

    override fun onVocalizerError(p0: Vocalizer?, p1: Error) {
        eventSender(SpeechEvent.OnSpeechError(task, p1))
    }

    override fun onSynthesisDone(p0: Vocalizer?, p1: Synthesis?) {

    }

    override fun onPlayingDone(p0: Vocalizer?) {
        eventSender(SpeechEvent.OnSpeechEnd(task))
    }

}