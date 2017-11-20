package com.punksta.apps.robopoetry.ext

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


/**
 * Created by stanislav on 11/20/17.
 */
fun Activity.hidekeyKoard() {
    val view = currentFocus
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}