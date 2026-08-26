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
    CHAOS("The ultimate test. Avoid spiky rings, unlock ghost rings, and break locks!"),
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
    CHROME("CHROME METAL", 25, listOf(Color(0xFFC0C0C0), Color(0xFFA9A9A9), Color(0xFF808080))),
    MINIMALIST("MINIMAL WHITE", 50, listOf(Color(0xFFFFFFFF), Color(0xFFF5F5F5), Color(0xFFDCDCDC)))
}

sealed class Screen {
    object Main : Screen()
    object ModeSelect : Screen()
    object Skins : Screen()
    object Tutorial : Screen()
    object Quests : Screen()
    data class LevelSelect(val mode: GameMode) : Screen()
    data class Game(val level: Int, val mode: GameMode) : Screen()
    object Options : Screen()
    object Credits : Screen()
}
