package com.punksta.apps.robopoetry.entity

import android.os.Parcelable
import paperparcel.PaperParcel
import paperparcel.PaperParcelable


/**
 * Created by stanislav on 3/9/17.
 */
sealed class ReadingTask(val yandexVoice: String) : Parcelable

@PaperParcel
class PoemReadingTask(yandexVoice: String,
                      val poem: Poem,
                      val writer: WriterInfo) : ReadingTask(yandexVoice), PaperParcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelPoemReadingTask.CREATOR
    }
}