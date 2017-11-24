package com.punksta.apps.robopoetry.service.util

import android.content.Context
import android.widget.Toast
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.service.entities.SpeechEvent

/**
 * Created by stanislav on 11/24/17.
 */
class ToastSpeechListener(
        private val context: Context,
        private val showOnlyErrors: Boolean) : OnSpeechListener {
    private var toast: Toast? = null

    override fun onEvent(speechEvent: SpeechEvent<*>) {
        when {
            showOnlyErrors &&
                    speechEvent is SpeechEvent.OnSpeechError -> {
                showToast(eventToMessage(speechEvent))
            }
            !showOnlyErrors -> showToast(eventToMessage(speechEvent))
        }
    }

    private fun eventToMessage(speechEvent: SpeechEvent<*>): String = when (speechEvent) {
        is SpeechEvent.OnSpeechError -> context.getString(R.string.play_error_message)
        is SpeechEvent.OnSpeechStart -> "Play started: ${speechEvent.task.title}"
        is SpeechEvent.OnSpeechStopped -> "Play stopped: ${speechEvent.task?.title}"
        is SpeechEvent.OnSpeechEnd -> "Play completed: ${speechEvent.task.title}"
        is SpeechEvent.OnProcessingStart -> "Processing started: ${speechEvent.task.title}"
        is SpeechEvent.ServiceStarted -> "Service started: ${speechEvent.task?.title}"
    }

    private fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                .also { it.show() }

    }
}