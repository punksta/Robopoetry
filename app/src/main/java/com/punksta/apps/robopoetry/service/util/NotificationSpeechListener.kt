package com.punksta.apps.robopoetry.service.util

import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.entities.SpeechEvent
import java.util.*

/**
 * Created by stanislav on 11/16/17.
 */
class NotificationSpeechListener(
        private val context: Context,
        private val stopPendingIntent: (SpeechTask) -> PendingIntent
) : ClosableOnSpeechListener {


    override fun onEvent(speechEvent: SpeechEvent) {
        when (speechEvent) {
            is SpeechEvent.OnSpeechStart -> {
                showNotification(speechEvent.task)
            }
            is SpeechEvent.OnProcessingStart -> {
                // skip
            }
            else -> {
                hideNotification()
            }
        }
    }

    override fun release() {
        hideNotification()
    }

    private val id: Int = Random(System.currentTimeMillis()).nextInt()

    private val builder = NotificationCompat.Builder(context, "speech_notification")
    private val manager = NotificationManagerCompat.from(context)

    private fun showNotification(speechTask: SpeechTask) {
        val notification = builder
                .setContentText(speechTask.title)
                .setContentText("Current play")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .addAction(
                        NotificationCompat.Action.Builder(
                                R.drawable.ic_stop_black_24dp,
                                "stop",
                                stopPendingIntent(speechTask)
                        ).build()

                )
                .build()

        manager.notify(id, notification)
    }

    private fun hideNotification() {
        id.let(manager::cancel)
    }
}