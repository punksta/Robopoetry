package com.punksta.apps.robopoetry.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by stanislav on 1/6/17.
 */

sealed class Voice {
    class YandexVoice(val voice: String) : Voice()
}

data class Robot(@DrawableRes val drawableId: Int,
                 @StringRes val nameId: Int,
                 val voice: Voice)