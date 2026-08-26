package com.senintrerus.circlelock.engine

import androidx.compose.ui.graphics.Color
import com.senintrerus.circlelock.model.CircleData
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.model.SkinType
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import kotlin.random.Random

object LevelGenerator {
    fun generateLevelCircles(level: Int, mode: GameMode = GameMode.STANDARD, skin: SkinType = SkinType.DEFAULT): List<CircleData> {
        val count = when {
            level <= 3 -> 2
            level <= 8 -> 3
            level <= 15 -> 4
            else -> 5
        }
        
        val skinColors = skin.colors
        
        return List(count) { i ->
            val linkedId = if (mode == GameMode.LINKED && count > 1) {
                if (i == 0) 1 else i - 1
            } else null

            CircleData(
                id = i,
                radius = 40f + (i * 30f),
                currentAngle = Random.nextInt(45, 315).toFloat(),
                color = skinColors.getOrElse(i % skinColors.size) { PrimaryGold },
                rotationSpeed = (1f - (level * 0.015f)).coerceAtLeast(0.4f),
                linkedCircleId = linkedId,
                linkRatio = if (Random.nextBoolean()) 0.5f else -0.5f,
                isSpiky = mode == GameMode.CHAOS && Random.nextFloat() < 0.3f,
                lockedByCircleId = if (mode == GameMode.CHAOS && i > 0 && Random.nextFloat() < 0.4f) i - 1 else null,
                isGhost = mode == GameMode.CHAOS && Random.nextFloat() < 0.2f
            )
        }
    }
}
