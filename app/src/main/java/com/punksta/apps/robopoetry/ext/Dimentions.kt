package com.punksta.apps.robopoetry.ext

import android.content.Context
import android.view.View

/**
 * Created by stanislav on 10/29/17.
 */
fun Context.dpFromPx(px: Float): Float = px / resources.displayMetrics.density
fun Context.pxFromDp(dp: Float): Float =dp * resources.displayMetrics.density

fun View.dpFromPx(px: Float) : Float = context.dpFromPx(px)
fun View.pxFromDp(px: Float) : Float = context.pxFromDp(px)