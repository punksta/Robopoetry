package com.punksta.apps.robopoetry.screens.common

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.screens.common.PlayingBroadcastReceiver.Companion.BRODCAST_ACTION
import ru.yandex.speechkit.Error
import ru.yandex.speechkit.SpeechKit
import ru.yandex.speechkit.Synthesis
import ru.yandex.speechkit.Vocalizer
import ru.yandex.speechkit.VocalizerListener


/**
 * Created by punksta on 1/3/17.
 */

const val key = "37064543-1d5c-4a88-80f6-d84ed607d874"

const val EXTRA_POEM = "text"
const val EXTRA_VOICE = "voice"
const val EXTRA_WRITER = "writer"
const val EXTRA_STATUS = "status"

const val ACTION_PLAY = "play"
const val ACTION_STOP = "stop"
const val ACTION_REGISTER = "register"

const val BROADCAST_PLAYING_BEGIN = "playing_begin"
const val BROADCAST_PLAYING_END = "playing_end"
const val BROADCAST_PLAYING_ERROR = "playing_error"


class PlayingService: Service(), VocalizerListener {
    private var poem: Poem? = null
    private var voice: String? = null
    private var writer: WriterInfo? = null
    private var lastAction: String? = null
    private var vocolizer: Vocalizer? = null
    private var player: MediaPlayer? = null
    private var first = false


    override fun onPlayingBegin(p0: Vocalizer?) {
        /*
         called while not done! Have to check last action
        */
        if (lastAction == BROADCAST_PLAYING_BEGIN)
            return

        lastAction = BROADCAST_PLAYING_BEGIN
        sendBroadcast(Intent(BRODCAST_ACTION)
                .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_BEGIN)
                .putExtra(EXTRA_POEM, poem)
                .putExtra(EXTRA_WRITER, writer)
        )
    }

    override fun onSynthesisDone(p0: Vocalizer?, p1: Synthesis?) {

    }

    override fun onPlayingDone(p0: Vocalizer?) {
        if (lastAction == BROADCAST_PLAYING_END)
            return

        lastAction = BROADCAST_PLAYING_END
        sendBroadcast(Intent(BRODCAST_ACTION)
                .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                .putExtra(EXTRA_POEM, poem)
                .putExtra(EXTRA_WRITER, writer)
        )
        stopBackgroundPlaying()
    }

    private fun stopBackgroundPlaying() {
        player?.pause()
    }


    private fun startBackgroundPlaying() {
        val afd = assets.openFd("music/sound1.mp3")
        if (player == null) {
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
                prepare()
                isLooping = true
            }
        }
        player?.start()
    }

    override fun onSynthesisBegin(p0: Vocalizer?) {
        startBackgroundPlaying()
    }

    override fun onVocalizerError(p0: Vocalizer?, p1: Error?) {
        Log.e(javaClass.simpleName, p1?.string)

        if (lastAction == BROADCAST_PLAYING_ERROR)
            return

        lastAction = BROADCAST_PLAYING_ERROR
        sendBroadcast(Intent(BRODCAST_ACTION)
                .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_ERROR)
                .putExtra(EXTRA_POEM, poem)
                .putExtra(EXTRA_WRITER, writer)
        )

        stopBackgroundPlaying()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        SpeechKit.getInstance().configure(this, key)
        Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, " ", true, Vocalizer.Voice.JANE).start()
    }


    override fun onDestroy() {
        super.onDestroy()
        vocolizer?.cancel()
        vocolizer = null
    }


    private fun clearVocalizer() {
        vocolizer?.cancel()
        vocolizer?.setListener(null)
        vocolizer = null
        lastAction = null
    }

    private fun onGetAction(intent: Intent?) {
        Log.v(javaClass.simpleName, intent?.action)

        when (intent?.action) {
            null, ACTION_STOP -> {
                clearVocalizer()
                stopBackgroundPlaying()
                sendBroadcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                        .putExtra(EXTRA_POEM, poem)
                        .putExtra(EXTRA_WRITER, writer)
                )
            }
            ACTION_REGISTER -> {
                if (lastAction != null) {
                    sendBroadcast(Intent(BRODCAST_ACTION)
                            .putExtra(EXTRA_STATUS, lastAction)
                            .putExtra(EXTRA_POEM, poem)
                            .putExtra(EXTRA_WRITER, writer)
                    )
                }
            }
            ACTION_PLAY -> {
                poem = intent?.getParcelableExtra(EXTRA_POEM)!!
                voice = intent?.getStringExtra(EXTRA_VOICE)!!
                writer = intent?.getParcelableExtra<WriterInfo>(EXTRA_WRITER)!!

                val text = "${writer?.name}. ${poem?.name}. ${poem?.сutText}. Конец"
                clearVocalizer()
                vocolizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, voice).apply {
                    setListener(this@PlayingService)
                    start()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onGetAction(intent)
        return START_STICKY
    }
}