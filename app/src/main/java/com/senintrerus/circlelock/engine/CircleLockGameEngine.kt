package com.senintrerus.circlelock.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.senintrerus.circlelock.model.CircleData
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import com.senintrerus.circlelock.ui.theme.SuccessGreen
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun CircleLockGameEngine(
    circles: List<CircleData>,
    onCirclesChanged: (List<CircleData>) -> Unit,
    isWin: Boolean,
    gameMode: GameMode = GameMode.STANDARD
) {
    var activeCircleId by remember { mutableStateOf<Int?>(null) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    
    val currentCircles by rememberUpdatedState(circles)
    val currentOnCirclesChanged by rememberUpdatedState(onCirclesChanged)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(350.dp)) {
            currentCircles.forEach { circle ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = circle.radius.dp.toPx() * 1.1f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        Canvas(
            modifier = Modifier
                .size(350.dp)
                .pointerInput(isWin) {
                    if (isWin) return@pointerInput
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitFirstDown()
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val startPos = event.position
                            val dist = sqrt((startPos.x - centerX) * (startPos.x - centerX) + (startPos.y - centerY) * (startPos.y - centerY))
                            
                            val closest = currentCircles.minByOrNull { circle ->
                                val circlePx = circle.radius.dp.toPx() * 1.1f
                                kotlin.math.abs(dist - circlePx)
                            }
                            
                            val threshold = 35.dp.toPx()
                            if (closest != null && kotlin.math.abs(dist - closest.radius.dp.toPx() * 1.1f) < threshold) {
                                activeCircleId = closest.id
                                touchPosition = startPos
                                
                                var currentAngle = Math.toDegrees(atan2(startPos.y - centerY, startPos.x - centerX).toDouble()).toFloat()
                                
                                do {
                                    val moveEvent = awaitPointerEvent()
                                    val dragChange = moveEvent.changes.firstOrNull()
                                    if (dragChange != null) {
                                        val newPos = dragChange.position
                                        touchPosition = newPos
                                        val newAngle = Math.toDegrees(atan2(newPos.y - centerY, newPos.x - centerX).toDouble()).toFloat()
                                        
                                        var diff = (newAngle - currentAngle)
                                        if (diff > 180) diff -= 360
                                        if (diff < -180) diff += 360
                                        
                                        currentOnCirclesChanged(currentCircles.map { circle ->
                                            when {
                                                circle.id == activeCircleId -> {
                                                    circle.copy(currentAngle = (circle.currentAngle + diff) % 360)
                                                }
                                                gameMode == GameMode.LINKED && circle.id == closest.linkedCircleId -> {
                                                    circle.copy(currentAngle = (circle.currentAngle + (diff * closest.linkRatio)) % 360)
                                                }
                                                else -> circle
                                            }
                                        })

                                        currentAngle = newAngle
                                        dragChange.consume()
                                    }
                                } while (moveEvent.changes.any { it.pressed })
                                
                                currentOnCirclesChanged(currentCircles.map {
                                    val normalized = (it.currentAngle % 360 + 360) % 360
                                    if (normalized < 8f || normalized > 352f) it.copy(currentAngle = 0f) else it
                                })
                                activeCircleId = null
                                touchPosition = null
                            }
                        }
                    }
                }
        ) {
            val center = this.center
            
            drawLine(
                color = if (isWin) SuccessGreen else PrimaryGold.copy(alpha = 0.2f),
                start = center,
                end = Offset(center.x + 160.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
            )

            currentCircles.forEach { circle ->
                val normalizedAngle = (circle.currentAngle % 360 + 360) % 360
                val isAligned = normalizedAngle < 8f || normalizedAngle > 352f
                val isActive = circle.id == activeCircleId
                
                val color = when {
                    isAligned -> SuccessGreen
                    isActive -> PrimaryGold
                    else -> circle.color.copy(alpha = 0.3f)
                }
                
                val strokeWidth = if (isActive) 22.dp.toPx() else 18.dp.toPx()
                val radiusPx = circle.radius.dp.toPx() * 1.1f

                if (isAligned) {
                    drawArc(
                        color = SuccessGreen.copy(alpha = 0.1f),
                        startAngle = circle.currentAngle + (circle.gapWidth / 2f),
                        sweepAngle = 360f - circle.gapWidth,
                        useCenter = false,
                        style = Stroke(width = strokeWidth + 8.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(radiusPx * 2, radiusPx * 2),
                        topLeft = Offset(center.x - radiusPx, center.y - radiusPx)
                    )
                }

                drawArc(
                    color = color,
                    startAngle = circle.currentAngle + (circle.gapWidth / 2f),
                    sweepAngle = 360f - circle.gapWidth,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(radiusPx * 2, radiusPx * 2),
                    topLeft = Offset(center.x - radiusPx, center.y - radiusPx)
                )
            }

            if (gameMode == GameMode.DARK && !isWin) {
                val touch = touchPosition
                if (touch != null) {
                    drawRect(
                        brush = Brush.radialGradient(
                            0.0f to Color.Transparent,
                            0.5f to Color.Black.copy(alpha = 0.98f),
                            center = touch,
                            radius = 120.dp.toPx()
                        ),
                        size = size
                    )
                } else {
                    drawRect(color = Color.Black.copy(alpha = 0.98f), size = size)
                }
            }
        }
    }
}
