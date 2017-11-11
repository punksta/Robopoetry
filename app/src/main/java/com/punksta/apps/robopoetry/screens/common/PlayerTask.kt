package com.punksta.apps.robopoetry.screens.common

import android.os.Parcelable
import com.punksta.apps.robopoetry.entity.Poem
import com.punksta.apps.robopoetry.entity.WriterInfo
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by stanislav on 3/8/17.
 */
sealed class PlayerTask(val voice: String) : Parcelable


@PaperParcel
class PoemTask(voice: String,
               val poem: Poem,
               val writer: WriterInfo) : PlayerTask(voice), PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelPoemTask.CREATOR
    }
}

@PaperParcel
class GreetingTask(voice: String,
                   val name: String,
                   val greeting: String) : PlayerTask(voice), PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelGreetingTask.CREATOR
    }
}