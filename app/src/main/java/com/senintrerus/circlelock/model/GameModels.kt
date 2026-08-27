package com.senintrerus.circlelock.model

import androidx.compose.ui.graphics.Color

data class CircleData(
    val id: Int,
    val radius: Float,
    var currentAngle: Float,
    val gapWidth: Float = 45f,
    val color: Color = Color.Gray,
    val rotationSpeed: Float = 1f,
    val linkedCircleId: Int? = null,
    val linkRatio: Float = 0.5f,
    val isSpiky: Boolean = false,
    val lockedByCircleId: Int? = null,
    val isGhost: Boolean = false
)

enum class GameMode(val description: String) {
    STANDARD("Classic lockpicking experience. Simple and satisfying."),
    DARK("Test your intuition. Only the area around your touch is visible."),
    TIME_ATTACK("Speed is key. Unlock before the timer runs out!"),
    LINKED("Ultimate puzzle. Rotating one ring affects others."),
    SWITCH("Chaotic challenge. The ring you can control changes every few seconds!"),
    ENDLESS("Endless challenge. Every lock you open adds time to the clock.");
    
    fun getDisplayName(): String = name.replace("_", " ")
}

data class DailyQuest(
    val id: String,
    val title: String,
    val target: Int,
    var current: Int,
    var isClaimed: Boolean = false
)

enum class SkinType(val displayName: String, val cost: Int, val colors: List<Color>) {
    DEFAULT("CLASSIC GOLD", 0, listOf(Color(0xFFFFD700), Color(0xFFC5A021), Color(0xFFCD7F32))),
    NEON("NEON NIGHTS", 10, listOf(Color(0xFF00FFFF), Color(0xFF00FF00), Color(0xFFFF00FF))),
    CYBER("CYBER PUNK", 15, listOf(Color(0xFFFF00FF), Color(0xFF00FFFF), Color(0xFFFF0080))),
    SUNSET("SUNSET GLOW", 20, listOf(Color(0xFFFF6B00), Color(0xFFFFAA00), Color(0xFFFF3D00))),
    EMERALD("EMERALD", 25, listOf(Color(0xFF00E676), Color(0xFF00C853), Color(0xFF1B5E20))),
    CHROME("CHROME METAL", 30, listOf(Color(0xFFC0C0C0), Color(0xFFA9A9A9), Color(0xFF808080))),
    RUBY("RUBY FLAME", 35, listOf(Color(0xFFFF1744), Color(0xFFD50000), Color(0xFFB71C1C))),
    FROST("ARCTIC FROST", 40, listOf(Color(0xFF80D8FF), Color(0xFF40C4FF), Color(0xFF00B0FF))),
    MIDNIGHT("MIDNIGHT", 50, listOf(Color(0xFF7C4DFF), Color(0xFF651FFF), Color(0xFF6200EA))),
    MINIMALIST("MINIMAL WHITE", 50, listOf(Color(0xFFFFFFFF), Color(0xFFF5F5F5), Color(0xFFDCDCDC))),
    ROYAL("GOLDEN EMPIRE", 75, listOf(Color(0xFFFFD700), Color(0xFFFFB300), Color(0xFFFF8F00))),
    RAINBOW("RAINBOW", 100, listOf(Color(0xFFFF1744), Color(0xFFFFEA00), Color(0xFF00E676), Color(0xFF2979FF), Color(0xFFFF00FF)))
}

enum class AnimationType(val displayName: String, val cost: Int, val description: String) {
    CLASSIC("Classic", 0, "Firework burst from center"),
    CONFETTI("Confetti", 15, "Falling confetti with gravity"),
    SPARKLE("Sparkle", 20, "Twinkling dots around center"),
    SHOCKWAVE("Shockwave", 30, "Expanding ring with particles"),
    FIRE("Fire", 40, "Flames rising upward"),
    SNOWFLAKE("Snowflake", 50, "Falling snow particles"),
    GALAXY("Galaxy", 75, "Swirling cosmic particles"),
    MATRIX("Matrix", 100, "Falling digital rain")
}

sealed class Screen {
    object Main : Screen()
    object ModeSelect : Screen()
    object Skins : Screen()
    object Tutorial : Screen()
    object Quests : Screen()
    object Streak : Screen()
    object Events : Screen()
    data class LevelSelect(val mode: GameMode) : Screen()
    data class Game(val level: Int, val mode: GameMode) : Screen()
    object Options : Screen()
    object Credits : Screen()
}
