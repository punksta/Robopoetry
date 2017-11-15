package com.punksta.apps.robopoetry.service.util

import com.punksta.apps.robopoetry.service.entities.SpeechEvent

interface OnSpeechListener {
    fun onEvent(speechEvent: SpeechEvent)
}

interface ClosableOnSpeechListener : OnSpeechListener {
    fun release()
}