package com.punksta.apps.robopoetry.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by stanislav on 1/1/17.
 */
data class Poem(val id: String,
                val name: String,
                val сutText: String,
                val year : String,
                val source: String) : Parcelable, EntityItem {
    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(id)
            writeString(name)
            writeString(сutText)
            writeString(year)
            writeString(source)
        }
    }

    constructor(id: Parcel) : this(id.readString(), id.readString(), id.readString(), id.readString(), id.readString())


    override fun describeContents(): Int = 0


    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Poem> = object : Parcelable.Creator<Poem> {
            override fun createFromParcel(`in`: Parcel): Poem {
                return Poem(`in`)
            }

            override fun newArray(size: Int): Array<Poem?> {
                return arrayOfNulls(size)
            }
        }
    }
}