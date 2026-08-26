package com.senintrerus.circlelock.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import com.senintrerus.circlelock.ui.theme.SuccessGreen
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val alpha: Float = 1f
)

@Composable
fun WinAnimation(isVisible: Boolean) {
    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val colors = listOf(PrimaryGold, Color.White, SuccessGreen, Color(0xFFFFF176))

    val particles = remember {
        List(70) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 6 + 1.5f
            Particle(
                x = 0f,
                y = 0f,
                vx = (Math.cos(angle) * speed).toFloat(),
                vy = (Math.sin(angle) * speed).toFloat(),
                size = Random.nextFloat() * 8 + 3,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        particles.forEach { p ->
            val currentX = center.x + p.vx * progress * 120
            val currentY = center.y + p.vy * progress * 120
            val currentAlpha = (1f - progress * 1.2f).coerceAtLeast(0f)

            drawCircle(
                color = p.color.copy(alpha = currentAlpha * 0.9f),
                radius = p.size * (1f - progress * 0.7f),
                center = Offset(currentX, currentY)
            )
        }
    }
}
