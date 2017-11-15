package com.punksta.apps.robopoetry.model

import com.punksta.apps.robopoetry.service.SpeechTask

/**
 * Created by stanislav on 11/15/17.
 */

interface TaskTextProvider {
    fun <T : SpeechTask> provide(speechTask: T): String
}
