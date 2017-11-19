package com.punksta.apps.robopoetry.ext

import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by stanislav on 11/18/17.
 */

typealias Size = Pair<Int, Int>

inline val Size.width
    get() = this.first

inline val Size.height
    get() = this.second

fun View.provideSize(callback: (Size) -> Unit) {
    when {
        width == 0 && height == 0 -> {
            val l = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback(width to height)
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener(l)
        }
        else -> {
            callback(width to height)
        }
    }
}

tailrec fun Array<View>.provideSize(callback: (Array<Size>) -> Unit) {
    when (size) {
        0 -> callback(emptyArray())
        1 -> {
            this[0].provideSize {
                callback(arrayOf(it))
            }
        }
        2 -> {
            this[0].provideSize { size1 ->
                this[1].provideSize { size2 ->
                    callback(arrayOf(size1, size2))
                }
            }
        }
        else -> {
            val head = this[0]
            val tail = this.copyOfRange(1, this.size)
            tail.provideSize { result ->
                head.provideSize { headSize ->
                    callback(arrayOf(headSize) + result)
                }
            }
        }
    }
}

fun calcSizeDifference(view: View, view2: View, callback: (Float) -> Unit) {
    arrayOf(view, view2).provideSize { (size1, size2) ->
        callback(
                Math.max(
                        size2.width / size1.width.toFloat(),
                        size2.height / size1.height.toFloat()
                )
        )
    }
}