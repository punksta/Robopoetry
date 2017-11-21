package com.punksta.apps.robopoetry.service.util

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.AsyncTask
import com.punksta.apps.robopoetry.screens.settings.Settings
import com.punksta.apps.robopoetry.service.entities.SpeechEvent

/**
 * Created by stanislav on 11/16/17.
 */

class PlayerListener(private val context: Context, private val mp3Path: String) : ClosableOnSpeechListener {

    private var player: MediaPlayer? = null

    private val sharedPref = context.applicationContext.getSharedPreferences(
            Settings.preferenceName, Context.MODE_PRIVATE)

    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener


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

        listener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            Settings.read(sharedPref, Settings.defaultSettings).let {
                onSettingsUpdate(it)
            }
        }

        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        Settings.read(sharedPref, Settings.defaultSettings).let {
            onSettingsUpdate(it)
        }
    }


    private var isDisabled = false
    private var shouldPlay = false;

    private fun onSettingsUpdate(settings: Settings) {
        isDisabled = !settings.playSound
        if (isDisabled) {
            pausePlay()
        } else if (shouldPlay) {
            startPlay()
        }
    }

    override fun release() {
        try {
            player?.release()
            sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onEvent(speechEvent: SpeechEvent<*>) {
        when (speechEvent) {
            is SpeechEvent.OnSpeechStart -> {
                shouldPlay = true
                startPlay()
            }
            is SpeechEvent.OnSpeechEnd, is SpeechEvent.OnSpeechError, is SpeechEvent.OnSpeechStopped -> {
                pausePlay()
                shouldPlay = false
            }
            else -> {
                //ignore
            }
        }
    }

    private fun startPlay() {
        if (!isDisabled) {
            if (player != null && player?.isPlaying == false)
                player?.start()
        }
    }

    private fun pausePlay() {
        if (player?.isPlaying == true)
            player?.pause()
    }
}
