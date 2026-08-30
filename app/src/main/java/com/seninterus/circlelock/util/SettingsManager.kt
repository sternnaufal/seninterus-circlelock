package com.seninterus.circlelock.util

import android.content.Context

object SettingsManager {
    private const val PREFS_NAME = "circle_lock_settings"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"

    fun isSoundEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isVibrationEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun isHapticEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAPTIC_ENABLED, true)
    }

    fun setHapticEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
    }

    fun isMusicEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_MUSIC_ENABLED, true)
    }

    fun setMusicEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()
    }
}
