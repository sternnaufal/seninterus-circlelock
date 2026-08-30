package com.seninterus.circlelock.util

import android.content.Context
import java.util.*

data class StreakInfo(
    val currentStreak: Int,
    val highestStreak: Int,
    val todayReward: Int,
    val hasClaimedToday: Boolean,
    val dayOfWeek: Int,
    val rewards: List<DayReward>
)

data class DayReward(
    val day: Int,
    val reward: Int,
    val isClaimed: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean
)

object StreakManager {
    private const val PREFS_NAME = "circle_lock_streak"
    private const val KEY_LAST_LOGIN_DATE = "last_login_date"
    private const val KEY_CURRENT_STREAK = "current_streak"
    private const val KEY_HIGHEST_STREAK = "highest_streak"
    private const val KEY_REWARDS_CLAIMED = "rewards_claimed"

    private val REWARD_TABLE = listOf(5, 10, 15, 20, 25, 30, 50)
    private val DAY_NAMES = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    fun onAppOpen(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastLogin = prefs.getLong(KEY_LAST_LOGIN_DATE, 0)
        val today = getTodayStart()

        if (lastLogin == 0L) {
            prefs.edit()
                .putLong(KEY_LAST_LOGIN_DATE, today)
                .putInt(KEY_CURRENT_STREAK, 1)
                .putInt(KEY_REWARDS_CLAIMED, 0)
                .apply()
            return
        }

        val daysDiff = ((today - lastLogin) / (24 * 60 * 60 * 1000)).toInt()

        when {
            daysDiff == 0 -> return
            daysDiff == 1 -> {
                val newStreak = prefs.getInt(KEY_CURRENT_STREAK, 1) + 1
                prefs.edit()
                    .putLong(KEY_LAST_LOGIN_DATE, today)
                    .putInt(KEY_CURRENT_STREAK, newStreak)
                    .putInt(KEY_REWARDS_CLAIMED, 0)
                    .apply()
                updateHighestStreak(context, newStreak)
            }
            else -> {
                prefs.edit()
                    .putLong(KEY_LAST_LOGIN_DATE, today)
                    .putInt(KEY_CURRENT_STREAK, 1)
                    .putInt(KEY_REWARDS_CLAIMED, 0)
                    .apply()
            }
        }
    }

    fun getStreakInfo(context: Context): StreakInfo {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 1)
        val highestStreak = prefs.getInt(KEY_HIGHEST_STREAK, 0)
        val claimedCount = prefs.getInt(KEY_REWARDS_CLAIMED, 0)
        val today = getTodayStart()
        val lastLogin = prefs.getLong(KEY_LAST_LOGIN_DATE, today)
        val isSameDay = today == lastLogin

        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val adjustedDayOfWeek = when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        val rewards = (0..6).map { i ->
            val dayNum = i + 1
            val rewardAmount = if (i < REWARD_TABLE.size) REWARD_TABLE[i] else 50
            val isClaimed = isSameDay && i < claimedCount
            val isToday = i == adjustedDayOfWeek
            val isFuture = i > adjustedDayOfWeek
            DayReward(dayNum, rewardAmount, isClaimed, isToday, isFuture)
        }

        val hasClaimedToday = claimedCount > 0 && isSameDay
        val todayReward = if (hasClaimedToday) 0 else getRewardForDay(currentStreak)

        return StreakInfo(
            currentStreak = currentStreak,
            highestStreak = highestStreak,
            todayReward = todayReward,
            hasClaimedToday = hasClaimedToday,
            dayOfWeek = adjustedDayOfWeek,
            rewards = rewards
        )
    }

    fun claimDailyReward(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val claimedCount = prefs.getInt(KEY_REWARDS_CLAIMED, 0)
        val today = getTodayStart()
        val lastLogin = prefs.getLong(KEY_LAST_LOGIN_DATE, today)

        if (today != lastLogin) return false
        if (claimedCount > 0) return false

        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 1)
        val reward = getRewardForDay(currentStreak)

        prefs.edit()
            .putInt(KEY_REWARDS_CLAIMED, claimedCount + 1)
            .apply()

        PlayerStats.addCurrency(context, reward)
        return true
    }

    private fun getRewardForDay(streak: Int): Int {
        val dayIndex = ((streak - 1) % 7).coerceIn(0, REWARD_TABLE.size - 1)
        return REWARD_TABLE[dayIndex]
    }

    private fun updateHighestStreak(context: Context, currentStreak: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val highest = prefs.getInt(KEY_HIGHEST_STREAK, 0)
        if (currentStreak > highest) {
            prefs.edit().putInt(KEY_HIGHEST_STREAK, currentStreak).apply()
        }
    }

    private fun getTodayStart(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
