package com.senintrerus.circlelock.util

import android.content.Context
import com.senintrerus.circlelock.model.GameMode
import java.util.*

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val mode: GameMode,
    val target: Int,
    var current: Int = 0,
    val reward: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val endsAt: Long = 0L
)

private data class ChallengeTemplate(
    val id: String,
    val title: String,
    val description: String,
    val mode: GameMode,
    val target: Int,
    val reward: Int
)

object EventManager {
    private const val PREFS_NAME = "circle_lock_events"
    private const val KEY_WEEK_START = "week_start"
    private const val KEY_CHALLENGE_ID = "challenge_id"
    private const val KEY_CHALLENGE_TITLE = "challenge_title"
    private const val KEY_CHALLENGE_DESC = "challenge_desc"
    private const val KEY_CHALLENGE_MODE = "challenge_mode"
    private const val KEY_CHALLENGE_TARGET = "challenge_target"
    private const val KEY_CHALLENGE_CURRENT = "challenge_current"
    private const val KEY_CHALLENGE_REWARD = "challenge_reward"
    private const val KEY_CHALLENGE_COMPLETED = "challenge_completed"
    private const val KEY_CHALLENGE_CLAIMED = "challenge_claimed"

    private val CHALLENGE_POOL = listOf(
        ChallengeTemplate("standard_speedrun", "Speed Demon", "Win 5 STANDARD levels", GameMode.STANDARD, 5, 30),
        ChallengeTemplate("dark_master", "Shadow Walker", "Win 5 DARK levels", GameMode.DARK, 5, 35),
        ChallengeTemplate("time_hunter", "Time Hunter", "Win 4 TIME_ATTACK levels", GameMode.TIME_ATTACK, 4, 40),
        ChallengeTemplate("chain_breaker", "Chain Breaker", "Win 3 LINKED levels", GameMode.LINKED, 3, 45),
        ChallengeTemplate("ring_master", "Ring Master", "Win 3 SWITCH levels", GameMode.SWITCH, 3, 35),
        ChallengeTemplate("endurance_king", "Endurance King", "Score 15 in ENDLESS mode", GameMode.ENDLESS, 15, 50),
        ChallengeTemplate("lock_opener", "Lock Opener", "Open 30 locks", GameMode.STANDARD, 30, 40),
        ChallengeTemplate("daily_player", "Dedicated Player", "Play 10 games any mode", GameMode.STANDARD, 10, 30),
    )

    fun onAppOpen(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val weekStart = prefs.getLong(KEY_WEEK_START, 0)
        val currentWeekStart = getWeekStart()

        if (weekStart != currentWeekStart) {
            generateNewChallenge(context)
            prefs.edit().putLong(KEY_WEEK_START, currentWeekStart).apply()
        }
    }

    fun getCurrentChallenge(context: Context): WeeklyChallenge {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val weekStart = prefs.getLong(KEY_WEEK_START, 0)
        val currentWeekStart = getWeekStart()

        if (weekStart != currentWeekStart) {
            generateNewChallenge(context)
        }

        val modeStr = prefs.getString(KEY_CHALLENGE_MODE, GameMode.STANDARD.name) ?: GameMode.STANDARD.name

        return WeeklyChallenge(
            id = prefs.getString(KEY_CHALLENGE_ID, "") ?: "",
            title = prefs.getString(KEY_CHALLENGE_TITLE, "") ?: "",
            description = prefs.getString(KEY_CHALLENGE_DESC, "") ?: "",
            mode = runCatching { GameMode.valueOf(modeStr) }.getOrDefault(GameMode.STANDARD),
            target = prefs.getInt(KEY_CHALLENGE_TARGET, 1),
            current = prefs.getInt(KEY_CHALLENGE_CURRENT, 0),
            reward = prefs.getInt(KEY_CHALLENGE_REWARD, 30),
            isCompleted = prefs.getBoolean(KEY_CHALLENGE_COMPLETED, false),
            isClaimed = prefs.getBoolean(KEY_CHALLENGE_CLAIMED, false),
            endsAt = getWeekEnd()
        )
    }

    fun updateProgress(context: Context, mode: GameMode, amount: Int = 1) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val challenge = getCurrentChallenge(context)

        if (challenge.isClaimed) return

        if (challenge.mode == GameMode.ENDLESS && mode == GameMode.ENDLESS) {
            val newCurrent = challenge.current + amount
            prefs.edit().putInt(KEY_CHALLENGE_CURRENT, newCurrent).apply()
            if (newCurrent >= challenge.target) {
                prefs.edit().putBoolean(KEY_CHALLENGE_COMPLETED, true).apply()
            }
        } else if (challenge.mode == mode) {
            val newCurrent = challenge.current + amount
            prefs.edit().putInt(KEY_CHALLENGE_CURRENT, newCurrent).apply()
            if (newCurrent >= challenge.target) {
                prefs.edit().putBoolean(KEY_CHALLENGE_COMPLETED, true).apply()
            }
        } else if (challenge.id == "daily_player" || challenge.id == "lock_opener") {
            val newCurrent = challenge.current + amount
            prefs.edit().putInt(KEY_CHALLENGE_CURRENT, newCurrent).apply()
            if (newCurrent >= challenge.target) {
                prefs.edit().putBoolean(KEY_CHALLENGE_COMPLETED, true).apply()
            }
        }
    }

    fun claimReward(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val challenge = getCurrentChallenge(context)

        if (!challenge.isCompleted || challenge.isClaimed) return false

        prefs.edit()
            .putBoolean(KEY_CHALLENGE_CLAIMED, true)
            .apply()

        PlayerStats.addCurrency(context, challenge.reward)
        return true
    }

    fun getTimeRemaining(): String {
        val now = System.currentTimeMillis()
        val end = getWeekEnd()
        val diff = end - now
        if (diff <= 0) return "Ended"

        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)

        return when {
            days > 0 -> "${days}d ${hours}h remaining"
            hours > 0 -> "${hours}h remaining"
            else -> "Ends today!"
        }
    }

    private fun generateNewChallenge(context: Context) {
        val challenge = CHALLENGE_POOL.random()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CHALLENGE_ID, challenge.id)
            .putString(KEY_CHALLENGE_TITLE, challenge.title)
            .putString(KEY_CHALLENGE_DESC, challenge.description)
            .putString(KEY_CHALLENGE_MODE, challenge.mode.name)
            .putInt(KEY_CHALLENGE_TARGET, challenge.target)
            .putInt(KEY_CHALLENGE_CURRENT, 0)
            .putInt(KEY_CHALLENGE_REWARD, challenge.reward)
            .putBoolean(KEY_CHALLENGE_COMPLETED, false)
            .putBoolean(KEY_CHALLENGE_CLAIMED, false)
            .apply()
    }

    private fun getWeekStart(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getWeekEnd(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            add(Calendar.DAY_OF_WEEK, 7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
