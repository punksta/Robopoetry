package com.punksta.apps.robopoetry.service.util

import android.content.Context
import android.media.MediaPlayer
import android.os.AsyncTask
import com.punksta.apps.robopoetry.service.entities.SpeechEvent

/**
 * Created by stanislav on 11/16/17.
 */

class PlayerListener(private val context: Context, private val mp3Path: String) : ClosableOnSpeechListener {

    private var player: MediaPlayer? = null


    init {
        class O : AsyncTask<Any, Any, MediaPlayer>() {
            override fun doInBackground(vararg p0: Any): MediaPlayer {
                val afd = context.assets.openFd(mp3Path)
                return MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    prepare()
                    isLooping = true
                }
            }

            override fun onPostExecute(result: MediaPlayer) {
                super.onPostExecute(result)
                player = result
            }
        }

        val r = O()
        r.execute()
    }

    override fun release() {
        try {
            player?.release()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }




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
        if (player != null && player?.isPlaying == false)
            player?.start()
    }

    private fun pausePlay() {
        player?.pause()
    }
}
