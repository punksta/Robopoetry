package com.punksta.apps.robopoetry.entity

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Created by stanislav on 1/1/17.
 */
data class WriterInfo(val id: String, val name: String, val poemCount: Int) : Parcelable {
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeInt(poemCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<WriterInfo> = object : Parcelable.Creator<WriterInfo> {
            override fun createFromParcel(`in`: Parcel): WriterInfo {
                return WriterInfo(`in`)
            }

            override fun newArray(size: Int): Array<WriterInfo?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(id: Parcel) : this(id.readString(), id.readString(), id.readInt())
}