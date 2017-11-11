package com.punksta.apps.robopoetry.entity

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by stanislav on 1/1/17.
 */
@PaperParcel
data class WriterInfo(val id: String, val name: String, val poemCount: Int) : Entity, PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelWriterInfo.CREATOR
    }
}
