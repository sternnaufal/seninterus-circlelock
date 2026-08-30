package com.seninterus.circlelock.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object RatingManager {
    private const val PREFS_NAME = "circle_lock_rating"
    private const val KEY_GAMES_PLAYED = "games_played"
    private const val KEY_RATED = "rated"
    private const val KEY_DISMISSED = "dismissed_count"
    private const val GAMES_BEFORE_PROMPT = 5
    private const val GAMES_BETWEEN_PROMPTS = 20

    fun onGameFinished(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_GAMES_PLAYED, 0)
        prefs.edit().putInt(KEY_GAMES_PLAYED, current + 1).apply()
    }

    fun shouldShowPrompt(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_RATED, false)) return false
        val dismissed = prefs.getInt(KEY_DISMISSED, 0)
        val gamesPlayed = prefs.getInt(KEY_GAMES_PLAYED, 0)
        if (dismissed == 0) return gamesPlayed >= GAMES_BEFORE_PROMPT
        return gamesPlayed >= GAMES_BETWEEN_PROMPTS
    }

    fun onUserRated(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_RATED, true).apply()
    }

    fun onUserDismissed(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_DISMISSED, 0)
        prefs.edit()
            .putInt(KEY_DISMISSED, current + 1)
            .putInt(KEY_GAMES_PLAYED, 0)
            .apply()
    }

    fun openPlayStore(activity: Activity) {
        val packageName = activity.packageName
        try {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            )
        } catch (e: Exception) {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }
}

@Composable
fun RatingDialogTrigger(
    context: Context,
    shouldShow: Boolean,
    onRate: () -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(shouldShow) {
        if (shouldShow && RatingManager.shouldShowPrompt(context)) {
            onRate()
        }
    }
}
