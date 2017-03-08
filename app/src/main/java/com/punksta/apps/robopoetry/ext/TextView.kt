package com.punksta.apps.robopoetry.ext

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import io.reactivex.Observable

/**
 * Created by stanislav on 1/2/17.
 */

private val cache = mutableMapOf<String, Typeface>()
private val monitor = Object()

private fun getTypeFace(name: String, context: Context): Typeface? {
    return synchronized(monitor) {
        if (cache.containsKey(name))
            cache[name]
        else {
            try {
                val t = Typeface.createFromAsset(context.assets, "fonts/$name")
                cache[name] = t
                t
            } catch (th: Throwable) {
                th.printStackTrace()
                null
            }
        }
    }
}

fun TextView.setTypeFace(name: String) {
    getTypeFace(name, context)?.let { typeface = it }
}


fun TextView.textChangesEvents(emmitFirst: Boolean = false) : Observable<String> {
    return Observable.create {

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                it.onNext(s?.toString()?.orEmpty())
            }
        }
        addTextChangedListener(textWatcher)
        if (emmitFirst) {
            it.onNext(text.toString())
        }
        it.setCancellable { removeTextChangedListener(textWatcher) }
    }
}