package com.punksta.apps.robopoetry.service.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.screens.writerLists.MainActivity
import com.punksta.apps.robopoetry.service.SpeechTask
import com.punksta.apps.robopoetry.service.entities.SpeechEvent

/**
 * Created by stanislav on 11/16/17.
 */
class NotificationSpeechListener(
        private val context: Service,
        private val stopPendingIntent: () -> PendingIntent
) : ClosableOnSpeechListener {

    private val channelId = "channel_play_status"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = channelId
            try {
                val nc = mNotificationManager.getNotificationChannel(id)
                if (nc != null) {
                    return
                }
            } catch (e: Throwable) {

            }

// The user-visible name of the channel.
            val name = context.getString(R.string.channel_name)
// The user-visible description of the channel.
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(id, name, importance)

            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.GREEN
            mChannel.enableVibration(false)
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onEvent(speechEvent: SpeechEvent<*>) {
        when (speechEvent) {
            is SpeechEvent.OnSpeechStart,
            is SpeechEvent.OnProcessingStart -> {
                showNotification(speechEvent.task!!)
            }

            else -> {
                hideNotification()
            }
        }
    }

    override fun release() {
        hideNotification()
    }

    private val id: Int = 9999

    private val stopIntent = stopPendingIntent()

    private val icon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

    private fun showNotification(speechTask: SpeechTask) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                    .setContentText(speechTask.title)
                    .setContentTitle("Now playing")
                    .setSmallIcon(R.drawable.ic_play_icon)
                    .setLargeIcon(icon)
                    .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))
                    .setTicker(speechTask.title)
                    .addAction(
                            NotificationCompat.Action.Builder(
                                    R.drawable.ic_stop_black_24dp,
                                    "stop",
                                    stopIntent
                            ).build()
                    )
                    .build()

            context.startForeground(id, notification)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun hideNotification() {
        try {
            context.stopForeground(true)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}