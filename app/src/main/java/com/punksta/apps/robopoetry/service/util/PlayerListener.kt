package com.punksta.apps.robopoetry.service.util

import android.content.Context
import android.media.MediaPlayer
import com.punksta.apps.robopoetry.service.entities.SpeechEvent

/**
 * Created by stanislav on 11/16/17.
 */

class PlayerListener(private val context: Context, private val mp3Path: String) : ClosableOnSpeechListener {
    override fun release() {
        try {
            player?.release()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private var player: MediaPlayer? = null


    override fun onEvent(speechEvent: SpeechEvent<*>) {
        when (speechEvent) {
            is SpeechEvent.OnSpeechStart -> {
                startPlay()
            }
            is SpeechEvent.OnSpeechEnd, is SpeechEvent.OnSpeechError, is SpeechEvent.OnSpeechStopped -> {
                pausePlay()
            }
            else -> {
                //ignore
            }
        }
    }

    private fun startPlay() {
        if (player == null) {
            val afd = context.assets.openFd(mp3Path)
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                isLooping = true
            }
        }
        if (!player!!.isPlaying)
            player?.start()
    }

    private fun pausePlay() {
        player?.pause()
    }
}
