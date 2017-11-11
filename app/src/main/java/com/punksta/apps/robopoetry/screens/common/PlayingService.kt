package com.punksta.apps.robopoetry.screens.common

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.screens.common.PlayingBroadcastReceiver.Companion.BRODCAST_ACTION
import ru.yandex.speechkit.*


/**
 * Created by punksta on 1/3/17.
 */

const val key = "37064543-1d5c-4a88-80f6-d84ed607d874"

const val EXTRA_POEM = "text"
const val EXTRA_CELEBRATION_ITEM = "celebration_item"
const val EXTRA_USER_NAME = "user_name"

const val EXTRA_VOICE = "voice"
const val EXTRA_WRITER = "writer"
const val EXTRA_CELEBRATION = "celebration"

const val EXTRA_ROBOT_NAME = "RobotName"
const val EXTRA_ROBOT_GREETING= "RobotGreeting"
const val EXTRA_STATUS = "status"

const val ACTION_PLAY = "play"
const val ACTION_STOP = "stop"
const val ACTION_REGISTER = "register"

const val BROADCAST_PLAYING_BEGIN = "playing_begin"
const val BROADCAST_PLAYING_END = "playing_end"
const val BROADCAST_PLAYING_ERROR = "playing_error"


class PlayingService: Service(), VocalizerListener {
    private var vocolizer: Vocalizer? = null
    private var player: MediaPlayer? = null
    private var first = false

    private var lastTask: PlayerTask? = null
    private var lastAction: String? = null


    override fun onPlayingBegin(p0: Vocalizer?) {
        /*
         called while not done! Have to check last action
        */
        if (lastAction == BROADCAST_PLAYING_BEGIN)
            return

        lastAction = BROADCAST_PLAYING_BEGIN
        val t = lastTask
        when (t) {
            is PoemTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_BEGIN)
                        .putExtra(EXTRA_POEM, t.poem)
                        .putExtra(EXTRA_WRITER, t.writer)
                )
            }

            is GreetingTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_BEGIN)
                        .putExtra(EXTRA_ROBOT_NAME, t.name)
                        .putExtra(EXTRA_ROBOT_GREETING, t.greeting)
                )
            }
        }

    }

    override fun onSynthesisDone(p0: Vocalizer?, p1: Synthesis?) {

    }

    override fun onPlayingDone(p0: Vocalizer?) {
        if (lastAction == BROADCAST_PLAYING_END)
            return
        lastAction = BROADCAST_PLAYING_END
        val t = lastTask
        when (t) {
            is PoemTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                        .putExtra(EXTRA_POEM, t.poem)
                        .putExtra(EXTRA_WRITER, t.writer)
                )
            }

            is GreetingTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                        .putExtra(EXTRA_ROBOT_NAME, t.name)
                        .putExtra(EXTRA_ROBOT_GREETING, t.greeting)
                )
            }
        }
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

        val t = lastTask
        when (t) {
            is PoemTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_ERROR)
                        .putExtra(EXTRA_POEM, t.poem)
                        .putExtra(EXTRA_WRITER, t.writer)
                )
            }
            is GreetingTask -> {
                sendStatusBrodcast(Intent(BRODCAST_ACTION)
                        .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_ERROR)
                        .putExtra(EXTRA_ROBOT_NAME, t.name)
                        .putExtra(EXTRA_ROBOT_GREETING, t.greeting)
                )
            }
        }

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


    private fun sendStatusBrodcast(intent: Intent) =
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    private fun onGetAction(intent: Intent?) {
        if (intent?.action != null)
            Log.v(javaClass.simpleName, intent.action)

        val t = lastTask

        when (intent?.action) {
            null, ACTION_STOP -> {
                clearVocalizer()
                stopBackgroundPlaying()
                when (t) {
                    is PoemTask -> {
                        sendStatusBrodcast(Intent(BRODCAST_ACTION)
                                .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                                .putExtra(EXTRA_POEM, t.poem)
                                .putExtra(EXTRA_WRITER, t.writer)
                        )
                    }

                    is GreetingTask -> {
                        sendStatusBrodcast(Intent(BRODCAST_ACTION)
                                .putExtra(EXTRA_STATUS, BROADCAST_PLAYING_END)
                                .putExtra(EXTRA_ROBOT_NAME, t.name)
                                .putExtra(EXTRA_ROBOT_GREETING, t.greeting)
                        )
                    }
                }
            }
            ACTION_REGISTER -> {
                if (lastAction != null) {
                    when (t) {
                        is PoemTask -> {
                            sendStatusBrodcast(Intent(BRODCAST_ACTION)
                                    .putExtra(EXTRA_STATUS, lastAction)
                                    .putExtra(EXTRA_POEM, t.poem)
                                    .putExtra(EXTRA_WRITER, t.writer)
                            )
                        }

                        is GreetingTask -> {
                            sendStatusBrodcast(Intent(BRODCAST_ACTION)
                                    .putExtra(EXTRA_STATUS, lastAction)
                                    .putExtra(EXTRA_ROBOT_NAME, t.name)
                                    .putExtra(EXTRA_ROBOT_GREETING, t.greeting)
                            )
                        }
                    }
                }
            }
            ACTION_PLAY -> {
                when {
                    intent.hasExtra(EXTRA_POEM) -> {
                        val poem = intent.getParcelableExtra<Poem>(EXTRA_POEM)!!
                        val  voice = intent.getStringExtra(EXTRA_VOICE)!!
                        val writer = intent.getParcelableExtra<WriterInfo>(EXTRA_WRITER)!!
                        lastTask = PoemTask(voice, poem, writer)

                        val text = "${writer.name}. ${poem.name}. ${poem.сutText}. Конец"
                        clearVocalizer()
                        vocolizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, voice).apply {
                            setListener(this@PlayingService)
                            start()
                        }
                    }

                    intent.hasExtra(EXTRA_ROBOT_NAME) -> {
                        val voice = intent.getStringExtra(EXTRA_VOICE)!!
                        val robotName = intent.getStringExtra(EXTRA_ROBOT_NAME)!!
                        val robotGreeting = intent.getStringExtra(EXTRA_ROBOT_GREETING)!!
                        lastTask = GreetingTask(voice, robotName, robotGreeting)
                        val text = robotGreeting
                        clearVocalizer()
                        vocolizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, voice).apply {
                            setListener(this@PlayingService)
                            start()
                        }
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onGetAction(intent)
        return START_STICKY
    }
}