package com.punksta.apps.robopoetry.screens.settings

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.os.Bundle
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.screens.writerLists.MainActivity
import com.punksta.apps.robopoetry.view.ThemeActivity
import kotlinx.android.synthetic.main.activity_settings.*


/**
 * Created by stanislav on 11/20/17.
 */
class SettingsActivity : ThemeActivity() {

    private lateinit var sharedPref: SharedPreferences

    private lateinit var currentTheme: Theme;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPref = application.getSharedPreferences(
                Settings.preferenceName, Context.MODE_PRIVATE)


        Settings.read(sharedPref, Settings.defaultSettings).let {
            currentTheme = it.theme
            this.renderSettings(it)
        }

        play_music_checkbox.setOnCheckedChangeListener { _, isSelected ->
            Settings.write(sharedPref, Settings.read(sharedPref, Settings.defaultSettings).copy(playSound = isSelected))
        }

        origin_theme.setOnClickListener {
            if (currentTheme != Theme.ORIGIN) {
                Settings.read(sharedPref, Settings.defaultSettings).copy(theme = Theme.ORIGIN).run {
                    Settings.write(sharedPref, this)
                    goToNewTheme(this)
                }
            }
        }

        neon_theme.setOnClickListener {
            if (currentTheme != Theme.NEON) {
                Settings.read(sharedPref, Settings.defaultSettings).copy(theme = Theme.NEON).run {
                    Settings.write(sharedPref, this)
                    goToNewTheme(this)
                }
            }
        }

        if (savedInstanceState == null) {
            if (intent.action == "change_theme") {
                //skip
            } else {
                overridePendingTransition(R.anim.right_in, R.anim.right_out)
            }
        }
    }


    private fun goToNewTheme(settings: Settings) {
        finish()
        TaskStackBuilder.create(this)
                .addNextIntent(Intent(this, MainActivity::class.java)
                        .addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                )
                .addNextIntent(Intent(this, SettingsActivity::class.java)
                        .setAction("change_theme"))
                .startActivities()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }

    override fun onStart() {
        super.onStart()

        sharedPref.registerOnSharedPreferenceChangeListener(sharedPreferenceListener)
    }

    private val sharedPreferenceListener = { preference: SharedPreferences, _: String ->
        val newSettings = Settings.read(preference, Settings.defaultSettings)
        renderSettings(newSettings)
    }


    override fun onStop() {
        super.onStop()
        sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPreferenceListener)
    }


    private fun renderSettings(settings: Settings) {
        if (play_music_checkbox.isChecked != settings.playSound) {
            play_music_checkbox.isChecked = settings.playSound
        }
    }
}