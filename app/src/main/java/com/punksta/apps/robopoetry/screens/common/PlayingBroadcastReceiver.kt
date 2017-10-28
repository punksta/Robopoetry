package com.punksta.apps.robopoetry.screens.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by stanislav on 1/3/17.
 */
class PlayingBroadcastReceiver : BroadcastReceiver() {

    companion object {
        @JvmStatic val BRODCAST_ACTION = "com.punksta.apps.robopoetry.brodcast.message"
    }

    private val subject: PublishSubject<Event> = PublishSubject.create()

    override fun onReceive(context: Context?, intent: Intent) {
        when (intent.getStringExtra(EXTRA_STATUS)) {
            BROADCAST_PLAYING_BEGIN -> {
                subject.onNext(Event.PlayingStartted())
            }
            else -> {
                subject.onNext(Event.PlayingComplete())
            }
        }
    }

    public fun observe(): Observable<Event> = subject

}

sealed class Event {
    class PlayingStartted() : Event()
    class PlayingComplete() : Event()
}