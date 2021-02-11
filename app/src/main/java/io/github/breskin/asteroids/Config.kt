package io.github.breskin.asteroids

import android.content.Context
import android.content.SharedPreferences

object Config {
    private const val SOUND = "sound"
    private const val MUSIC = "music"
    private const val VIBRATIONS = "vibrations"
    private const val CONTROLS = "controls"

    private lateinit var preferences: SharedPreferences

    private var _vibrationsEnabled = true
    private var _musicEnabled = true
    private var _soundEnabled = true
    private var _oneHandedControls = false

    private var musicChangedCallback: (() -> Unit)? = null

    fun setMusicChangedCallback(callback: () -> Unit) {
        this.musicChangedCallback = callback
    }

    fun load(context: Context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        _vibrationsEnabled = preferences.getBoolean(VIBRATIONS, true)
        _musicEnabled = preferences.getBoolean(MUSIC, true)
        _soundEnabled = preferences.getBoolean(SOUND, true)
        _oneHandedControls = preferences.getBoolean(CONTROLS, false)
    }

    var vibrationsEnabled
        get() = _vibrationsEnabled
        set(value) {
            _vibrationsEnabled = value

            preferences.edit().putBoolean(VIBRATIONS, value).apply()
        }

    var musicEnabled
        get() = _musicEnabled
        set(value) {
            _musicEnabled = value

            preferences.edit().putBoolean(MUSIC, value).apply()

            musicChangedCallback?.invoke()
        }

    var soundEnabled
        get() = _soundEnabled
        set(value) {
            _soundEnabled = value

            preferences.edit().putBoolean(SOUND, value).apply()
        }

    var oneHandedControls
        get() = _oneHandedControls
        set(value) {
            _oneHandedControls = value

            preferences.edit().putBoolean(CONTROLS, value).apply()
        }
}