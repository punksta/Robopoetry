package com.punksta.apps.robopoetry.entity

import android.os.Parcel
import android.os.Parcelable
import com.punksta.apps.robopoetry.R


/**
 * Created by stanislav on 3/8/17.
 */
interface Celebration : Entity {
    val name : Int
}

object March8 : Celebration {
    @JvmField val CREATOR: Parcelable.Creator<March8> = object : Parcelable.Creator<March8> {
        override fun createFromParcel(`in`: Parcel): March8 {
            return March8
        }

        override fun newArray(size: Int): Array<March8?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0;
    }

    override val name: Int
        get() = R.string.mart8

}