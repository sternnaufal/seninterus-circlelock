package com.senintrerus.circlelock.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.senintrerus.circlelock.model.AnimationType
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import com.senintrerus.circlelock.ui.theme.SuccessGreen
import kotlin.math.cos
import kotlin.math.sin
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
fun WinAnimation(
    isVisible: Boolean,
    style: AnimationType = AnimationType.CLASSIC,
    skinColors: List<Color> = listOf(PrimaryGold, Color.White, SuccessGreen, Color(0xFFFFF176))
) {
    if (!isVisible) return

    when (style) {
        AnimationType.CLASSIC -> ClassicAnimation(skinColors)
        AnimationType.CONFETTI -> ConfettiAnimation(skinColors)
        AnimationType.SPARKLE -> SparkleAnimation(skinColors)
        AnimationType.SHOCKWAVE -> ShockwaveAnimation(skinColors)
        AnimationType.FIRE -> FireAnimation()
        AnimationType.SNOWFLAKE -> SnowflakeAnimation()
        AnimationType.GALAXY -> GalaxyAnimation()
        AnimationType.MATRIX -> MatrixAnimation()
    }
}

@Composable
private fun ClassicAnimation(colors: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "classic")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val particles = remember {
        List(70) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 6 + 1.5f
            Particle(
                x = 0f, y = 0f,
                vx = (cos(angle) * speed).toFloat(),
                vy = (sin(angle) * speed).toFloat(),
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

@Composable
private fun ConfettiAnimation(colors: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val confetti = remember {
        List(80) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 4 + 2f
            Particle(
                x = 0f, y = 0f,
                vx = (cos(angle) * speed).toFloat(),
                vy = (sin(angle) * speed).toFloat() - Random.nextFloat() * 2,
                size = Random.nextFloat() * 6 + 4,
                color = colors[Random.nextInt(colors.size)],
                alpha = Random.nextFloat() * 0.3f + 0.7f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val gravity = progress * progress * 200f

        confetti.forEach { p ->
            val currentX = center.x + p.vx * progress * 100
            val currentY = center.y + p.vy * progress * 100 + gravity
            val currentAlpha = (1f - progress * 0.8f).coerceAtLeast(0f)

            drawRect(
                color = p.color.copy(alpha = currentAlpha * p.alpha),
                topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                size = Size(p.size, p.size / 2)
            )
        }
    }
}

@Composable
private fun SparkleAnimation(colors: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val sparkles = remember {
        List(90) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val dist = Random.nextFloat() * 180f + 20f
            Particle(
                x = (cos(angle) * dist).toFloat(),
                y = (sin(angle) * dist).toFloat(),
                vx = 0f, vy = 0f,
                size = Random.nextFloat() * 4 + 1.5f,
                color = colors[Random.nextInt(colors.size)],
                alpha = Random.nextFloat()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        sparkles.forEachIndexed { index, p ->
            val delay = (index % 10) * 0.1f
            val localProgress = ((progress - delay) / (1f - delay)).coerceIn(0f, 1f)
            val twinkle = (sin(localProgress * Math.PI * 6).toFloat() + 1f) / 2f
            val fadeIn = (localProgress * 5f).coerceAtMost(1f)
            val fadeOut = (1f - (localProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)
            val alpha = twinkle * fadeIn * fadeOut

            if (alpha > 0.05f) {
                drawCircle(
                    color = p.color.copy(alpha = alpha * 0.9f),
                    radius = p.size * (0.5f + twinkle * 0.5f),
                    center = Offset(center.x + p.x, center.y + p.y)
                )
            }
        }
    }
}

@Composable
private fun ShockwaveAnimation(colors: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "shockwave")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val ringParticles = remember {
        List(50) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 2 + 0.5f
            Particle(
                x = 0f, y = 0f,
                vx = (cos(angle) * speed).toFloat(),
                vy = (sin(angle) * speed).toFloat(),
                size = Random.nextFloat() * 3 + 1f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2f

        val ringRadius = maxRadius * progress * 1.2f
        val ringAlpha = (1f - progress).coerceAtLeast(0f)
        drawCircle(
            color = Color.White.copy(alpha = ringAlpha * 0.6f),
            radius = ringRadius,
            center = center,
            style = Stroke(width = 4f * (1f - progress) + 1f)
        )

        if (progress < 0.3f) {
            val flashAlpha = (1f - progress / 0.3f) * 0.4f
            drawCircle(
                color = Color.White.copy(alpha = flashAlpha),
                radius = maxRadius * 0.3f * (1f - progress / 0.3f),
                center = center
            )
        }

        ringParticles.forEach { p ->
            val px = center.x + p.vx * progress * maxRadius * 0.8f
            val py = center.y + p.vy * progress * maxRadius * 0.8f
            val pAlpha = (1f - progress * 1.3f).coerceAtLeast(0f)
            drawCircle(
                color = p.color.copy(alpha = pAlpha * 0.7f),
                radius = p.size * (1f - progress * 0.5f),
                center = Offset(px, py)
            )
        }
    }
}

@Composable
private fun FireAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val fireColors = listOf(
        Color(0xFFFF6D00), Color(0xFFFF9100), Color(0xFFFFAB00),
        Color(0xFFFFD600), Color(0xFFFF1744)
    )

    val flames = remember {
        List(60) {
            val spread = Random.nextFloat() * 2 - 1f
            Particle(
                x = spread * 40f, y = 0f,
                vx = spread * (Random.nextFloat() * 1.5f),
                vy = -(Random.nextFloat() * 5 + 3f),
                size = Random.nextFloat() * 10 + 5f,
                color = fireColors[Random.nextInt(fireColors.size)],
                alpha = Random.nextFloat() * 0.4f + 0.6f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2 + 60f)
        flames.forEach { p ->
            val life = (progress + Random.nextFloat() * 0.2f) % 1f
            val px = center.x + p.vx * life * 80
            val py = center.y + p.vy * life * 100
            val alpha = (1f - life) * p.alpha
            val radius = p.size * (1f - life * 0.6f) * (0.5f + sin(life * Math.PI).toFloat() * 0.5f)
            if (alpha > 0.05f && radius > 0.5f) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(px, py)
                )
            }
        }
    }
}

@Composable
private fun SnowflakeAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val snowColors = listOf(
        Color.White, Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFF90CAF9)
    )

    val flakes = remember {
        List(100) {
            Particle(
                x = Random.nextFloat() * 1f - 0.5f,
                y = -Random.nextFloat() * 0.3f,
                vx = (Random.nextFloat() - 0.5f) * 0.5f,
                vy = Random.nextFloat() * 2 + 1f,
                size = Random.nextFloat() * 4 + 2f,
                color = snowColors[Random.nextInt(snowColors.size)],
                alpha = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        flakes.forEach { p ->
            val wobble = sin(progress * Math.PI * 4 + p.x * 10).toFloat() * 15f
            val px = size.width / 2 + p.x * size.width + p.vx * progress * 200 + wobble
            val py = size.height * 0.1f + p.y * size.height + p.vy * progress * size.height * 0.8f
            val fadeIn = (progress * 5f).coerceAtMost(1f)
            val fadeOut = (1f - (progress - 0.8f) / 0.2f).coerceIn(0f, 1f)
            val alpha = fadeIn * fadeOut * p.alpha

            if (alpha > 0.05f && py in 0f..size.height) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size,
                    center = Offset(px, py)
                )
            }
        }
    }
}

@Composable
private fun GalaxyAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val galaxyColors = listOf(
        Color(0xFF7C4DFF), Color(0xFF651FFF), Color(0xFF6200EA),
        Color(0xFFE040FB), Color(0xFF00BCD4), Color(0xFF00E5FF)
    )

    val stars = remember {
        List(120) {
            val arm = Random.nextInt(3)
            val armOffset = arm * (2 * Math.PI / 3)
            val dist = Random.nextFloat() * 150f + 10f
            val angle = armOffset + dist * 0.02 + Random.nextFloat() * 0.5f
            Particle(
                x = (cos(angle) * dist).toFloat(),
                y = (sin(angle) * dist).toFloat(),
                vx = 0f, vy = 0f,
                size = Random.nextFloat() * 3 + 1f,
                color = galaxyColors[Random.nextInt(galaxyColors.size)],
                alpha = Random.nextFloat() * 0.6f + 0.4f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val rotation = progress * 360f

        stars.forEach { p ->
            val rad = Math.toRadians(rotation.toDouble()).toFloat()
            val rx = p.x * cos(rad) - p.y * sin(rad)
            val ry = p.x * sin(rad) + p.y * cos(rad)
            val px = center.x + rx
            val py = center.y + ry
            val fadeIn = (progress * 4f).coerceAtMost(1f)
            val fadeOut = (1f - (progress - 0.75f) / 0.25f).coerceIn(0f, 1f)
            val alpha = fadeIn * fadeOut * p.alpha
            val twinkle = (sin(progress * 20 + p.x).toFloat() + 1f) / 2f

            if (alpha > 0.05f && px in 0f..size.width && py in 0f..size.height) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size * (0.7f + twinkle * 0.3f),
                    center = Offset(px, py)
                )
            }
        }
    }
}

@Composable
private fun MatrixAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val matrixGreen = listOf(
        Color(0xFF00FF41), Color(0xFF00CC33), Color(0xFF009926),
        Color(0xFF33FF66), Color(0xFF00E639)
    )

    data class MatrixChar(val x: Float, val speed: Float, val size: Float, val color: Color, val char: Char)

    val chars = remember {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&"
        List(60) {
            MatrixChar(
                x = Random.nextFloat(),
                speed = Random.nextFloat() * 3 + 2f,
                size = Random.nextFloat() * 8 + 10f,
                color = matrixGreen[Random.nextInt(matrixGreen.size)],
                char = alphabet[Random.nextInt(alphabet.length)]
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        chars.forEach { c ->
            val px = c.x * size.width
            val py = -size.height * 0.2f + progress * c.speed * size.height
            val fadeIn = (progress * 3f).coerceAtMost(1f)
            val fadeOut = (1f - (progress - 0.8f) / 0.2f).coerceIn(0f, 1f)
            val alpha = fadeIn * fadeOut

            if (alpha > 0.05f && py in -50f..size.height + 50f) {
                drawCircle(
                    color = c.color.copy(alpha = alpha),
                    radius = c.size / 2,
                    center = Offset(px, py)
                )
            }
        }
    }
}
