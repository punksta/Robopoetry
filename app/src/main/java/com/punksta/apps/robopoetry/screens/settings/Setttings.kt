package com.punksta.apps.robopoetry.screens.settings

import android.content.SharedPreferences
import com.punksta.apps.robopoetry.R
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by stanislav on 11/20/17.
 */
enum class Theme(val themeRes: Int) : Serializable {
    ORIGIN(R.style.AppTheme_Origin),
    NEON(R.style.AppTheme_Neon)
}

data class Settings(
        val theme: Theme,
        val playSound: Boolean
) : Serializable {
    companion object {
        val defaultSettings = Settings(Theme.ORIGIN, true)
        val preferenceName = "appSettings"
        private val objectKey = "settings"

        fun read(pref: SharedPreferences, default: Settings): Settings {
            val string = pref.getString(objectKey, null)
            return when (string) {
                null -> default
                else -> {
                    val json = JSONObject(string)
                    return Settings(Theme.valueOf(json.getString("theme")), json.getBoolean("music"))
                }
            }
        }

        fun write(pref: SharedPreferences, settings: Settings) {
            val json = JSONObject(mapOf("theme" to settings.theme.name, "music" to settings.playSound))

            pref.edit().putString(objectKey, json.toString()).apply()
        }
    }
}


