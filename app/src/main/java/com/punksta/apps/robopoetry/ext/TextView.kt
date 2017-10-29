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


fun TextView.textChangesEvents(emmitFirst: Boolean = false) : Observable<String> {
    return Observable.create {

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                it.onNext(s?.toString().orEmpty())
            }
        }
        addTextChangedListener(textWatcher)
        if (emmitFirst) {
            it.onNext(text.toString())
        }
        it.setCancellable { removeTextChangedListener(textWatcher) }
    }
}