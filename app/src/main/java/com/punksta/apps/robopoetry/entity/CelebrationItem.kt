package com.punksta.apps.robopoetry.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by stanislav on 1/1/17.
 */
data class CelebrationItem(val celebrationText: String) : EntityItem {
    override fun writeToParcel(dest: Parcel, flags: Int) {
        with(dest) {
            writeString(celebrationText)
        }
    }

    constructor(id: Parcel) : this(id.readString())


    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CelebrationItem> = object : Parcelable.Creator<CelebrationItem> {
            override fun createFromParcel(`in`: Parcel): CelebrationItem {
                return CelebrationItem(`in`)
            }

            override fun newArray(size: Int): Array<CelebrationItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}