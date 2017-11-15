package com.punksta.apps.robopoetry.entity

import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.entities.YandexVoice
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by stanislav on 11/15/17.
 */
@PaperParcel
data class GreetingsSpeechTask(
        val text: String,
        override val voice: YandexVoice,
        override val title: String
) : SpeechTask, PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelGreetingsSpeechTask.CREATOR
    }

}