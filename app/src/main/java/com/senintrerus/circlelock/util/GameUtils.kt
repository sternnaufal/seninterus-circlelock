package com.senintrerus.circlelock.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.senintrerus.circlelock.util.SettingsManager

enum class VibrationType {
    CLICK, SUCCESS, ERROR
}

fun vibrateDevice(context: Context, type: VibrationType = VibrationType.CLICK) {
    if (!SettingsManager.isVibrationEnabled(context)) return
    if (type == VibrationType.CLICK && !SettingsManager.isHapticEnabled(context)) return

    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    VibrationType.CLICK -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        } else {
                            VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
                        }
                    }
                    VibrationType.SUCCESS -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
                }
                it.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    VibrationType.CLICK -> it.vibrate(10)
                    VibrationType.SUCCESS -> it.vibrate(200)
                    VibrationType.ERROR -> it.vibrate(longArrayOf(0, 50, 50, 50), -1)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

object PlayerStats {
    private const val PREFS_NAME = "circle_lock_stats"
    private const val KEY_TOTAL_CLEARED = "total_cleared"
    private const val KEY_ACTIVE_SKIN = "active_skin"
    private const val KEY_UNLOCKED_SKINS = "unlocked_skins"

    fun incrementClearedCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_TOTAL_CLEARED, 0)
        prefs.edit().putInt(KEY_TOTAL_CLEARED, current + 1).apply()
    }

    fun getTotalClearedCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_TOTAL_CLEARED, 0)
    }

    fun getActiveSkin(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_SKIN, "DEFAULT") ?: "DEFAULT"
    }

    fun setActiveSkin(context: Context, skinName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACTIVE_SKIN, skinName).apply()
    }

    fun isSkinUnlocked(context: Context, skinName: String): Boolean {
        if (skinName == "DEFAULT") return true
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val unlocked = prefs.getStringSet(KEY_UNLOCKED_SKINS, setOf("DEFAULT")) ?: setOf("DEFAULT")
        return unlocked.contains(skinName)
    }

    fun unlockSkin(context: Context, skinName: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val unlocked = prefs.getStringSet(KEY_UNLOCKED_SKINS, setOf("DEFAULT"))?.toMutableSet() ?: mutableSetOf("DEFAULT")
        unlocked.add(skinName)
        prefs.edit().putStringSet(KEY_UNLOCKED_SKINS, unlocked).apply()
    }
}
