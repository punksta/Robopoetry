package com.punksta.apps.robopoetry.entity

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by stanislav on 1/1/17.
 */
@PaperParcel
data class Poem(val id: String,
                val name: String,
                val сutText: String,
                val year : String,
                val source: String) : PaperParcelable, EntityItem {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPoem.CREATOR
    }
}