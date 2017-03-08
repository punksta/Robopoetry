package com.punksta.apps.robopoetry.model

import android.content.Context

/**
 * Created by stanislav on 1/2/17.
 */
object Provider {
    var model : DataModel? = null

    fun getModel(context: Context):DataModel {
        return synchronized(this) {
            if (model == null) {
                model = DataModelImp(context)
            }
            model!!
        }
    }
}


fun Context.getModel() = Provider.getModel(this)