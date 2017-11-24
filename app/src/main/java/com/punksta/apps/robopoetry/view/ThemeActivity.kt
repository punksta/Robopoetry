package com.punksta.apps.robopoetry.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.punksta.apps.robopoetry.screens.settings.Settings

/**
 * Created by stanislav on 11/21/17.
 */
abstract class ThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        application.getSharedPreferences(
                Settings.preferenceName, Context.MODE_PRIVATE)
                .run { Settings.read(this, Settings.defaultSettings) }
                .run { setTheme(this.theme.themeRes) }
        super.onCreate(savedInstanceState)
    }
}