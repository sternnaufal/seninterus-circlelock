package com.senintrerus.circlelock.util

import android.content.Context
import com.senintrerus.circlelock.model.DailyQuest
import java.util.*

object QuestManager {
    private const val PREFS_NAME = "circle_lock_quests"
    private const val KEY_LAST_DATE = "last_quest_date"
    
    private val ALL_QUESTS = listOf(
        DailyQuest("1", "Open 10 Locks", 10, 0),
        DailyQuest("2", "Win 3 Chaos Games", 3, 0),
        DailyQuest("3", "Open 20 Locks", 20, 0),
        DailyQuest("4", "Play 5 Games", 5, 0),
        DailyQuest("5", "Win 2 Time Attack Games", 2, 0)
    )

    fun getDailyQuests(context: Context): List<DailyQuest> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastDate = prefs.getLong(KEY_LAST_DATE, 0)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (today > lastDate) {
            // New day, generate new quests
            val newQuests = ALL_QUESTS.shuffled().take(3)
            saveQuests(context, newQuests)
            prefs.edit().putLong(KEY_LAST_DATE, today).apply()
            return newQuests
        }

        // Load existing quests
        return loadQuests(context)
    }

    private fun saveQuests(context: Context, quests: List<DailyQuest>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        quests.forEachIndexed { i, q ->
            editor.putString("q_${i}_id", q.id)
            editor.putString("q_${i}_title", q.title)
            editor.putInt("q_${i}_target", q.target)
            editor.putInt("q_${i}_current", q.current)
            editor.putBoolean("q_${i}_claimed", q.isClaimed)
        }
        editor.apply()
    }

    private fun loadQuests(context: Context): List<DailyQuest> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return (0..2).map { i ->
            DailyQuest(
                id = prefs.getString("q_${i}_id", "") ?: "",
                title = prefs.getString("q_${i}_title", "") ?: "",
                target = prefs.getInt("q_${i}_target", 1),
                current = prefs.getInt("q_${i}_current", 0),
                isClaimed = prefs.getBoolean("q_${i}_claimed", false)
            )
        }
    }

    fun updateProgress(context: Context, questTitlePart: String, amount: Int = 1) {
        val quests = loadQuests(context).toMutableList()
        var changed = false
        quests.forEachIndexed { i, q ->
            if (q.title.contains(questTitlePart, ignoreCase = true) && !q.isClaimed) {
                q.current = (q.current + amount).coerceAtMost(q.target)
                changed = true
            }
        }
        if (changed) saveQuests(context, quests)
    }

    fun claimReward(context: Context, index: Int): Boolean {
        val quests = loadQuests(context).toMutableList()
        val q = quests[index]
        if (q.current >= q.target && !q.isClaimed) {
            q.isClaimed = true
            saveQuests(context, quests)
            // Reward: Add to total cleared count or something
            PlayerStats.incrementClearedCount(context) 
            return true
        }
        return false
    }
}
