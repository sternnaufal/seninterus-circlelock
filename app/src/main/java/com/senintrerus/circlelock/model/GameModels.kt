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
    val linkRatio: Float = 0.5f
)

enum class GameMode(val description: String) {
    STANDARD("Classic lockpicking experience. Simple and satisfying."),
    DARK("Test your intuition. Only the area around your touch is visible."),
    TIME_ATTACK("Speed is key. Unlock before the timer runs out!"),
    LINKED("Ultimate puzzle. Rotating one ring affects others.");
    
    fun getDisplayName(): String = name.replace("_", " ")
}

sealed class Screen {
    object Main : Screen()
    object ModeSelect : Screen()
    data class LevelSelect(val mode: GameMode) : Screen()
    data class Game(val level: Int, val mode: GameMode) : Screen()
    object Options : Screen()
    object Credits : Screen()
}
