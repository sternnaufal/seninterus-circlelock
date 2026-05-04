package com.senintrerus.circlelock.engine

import androidx.compose.ui.graphics.Color
import com.senintrerus.circlelock.model.CircleData
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import kotlin.random.Random

object LevelGenerator {
    fun generateLevelCircles(level: Int, mode: GameMode = GameMode.STANDARD): List<CircleData> {
        val count = when {
            level <= 3 -> 2
            level <= 8 -> 3
            level <= 15 -> 4
            else -> 5
        }
        
        val colors = listOf(
            Color(0xFFCD7F32), // Bronze
            Color(0xFFC0C0C0), // Silver
            PrimaryGold,       // Gold
            Color(0xFFE5E4E2), // Platinum
            Color(0xFFB4CFEC)  // Diamond
        )
        
        return List(count) { i ->
            val linkedId = if (mode == GameMode.LINKED && count > 1) {
                if (i == 0) 1 else i - 1
            } else null

            CircleData(
                id = i,
                radius = 40f + (i * 30f),
                currentAngle = Random.nextInt(45, 315).toFloat(),
                color = colors.getOrElse(i) { Color.Cyan },
                rotationSpeed = (1f - (level * 0.015f)).coerceAtLeast(0.4f),
                linkedCircleId = linkedId,
                linkRatio = if (Random.nextBoolean()) 0.5f else -0.5f
            )
        }
    }
}
