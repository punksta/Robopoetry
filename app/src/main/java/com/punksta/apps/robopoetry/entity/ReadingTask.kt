package com.punksta.apps.robopoetry.entity

import paperparcel.PaperParcel
import paperparcel.PaperParcelable


/**
 * Created by stanislav on 3/9/17.
 */
abstract class ReadingTask(val yandexVoice: String) : PaperParcelable

@PaperParcel
class PoemReadingTask(yandexVoice: String,
                      val poem: Poem,
                      val writer: WriterInfo) : ReadingTask(yandexVoice) {

    companion object {
        @JvmField val CREATOR = PaperParcelPoemReadingTask.CREATOR
    }
}

@PaperParcel
class CelebrationReadingTask(yandexVoice: String,
                             val userName: String,
                             val congratulation: Celebration,
                             val celebrationItem: CelebrationItem) : ReadingTask(yandexVoice) {
    companion object {
        @JvmField val CREATOR = PaperParcelCelebrationReadingTask.CREATOR
    }
}