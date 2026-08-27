package com.senintrerus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.ui.components.MenuButton
import com.senintrerus.circlelock.ui.theme.*
import com.senintrerus.circlelock.util.PlayerStats

@Composable
fun MainScreen(
    onPlayClick: () -> Unit,
    onSkinsClick: () -> Unit,
    onTutorialClick: () -> Unit,
    onQuestsClick: () -> Unit,
    onStreakClick: () -> Unit,
    onEventsClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val context = LocalContext.current
    val totalCleared = PlayerStats.getTotalClearedCount(context)

    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.03f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PrimaryGold.copy(alpha = glowAlpha), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 1.3f
                ),
                radius = size.minDimension / 2f * glowScale
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(glowScale)
                        .blur(20.dp)
                        .border(2.dp, PrimaryGold.copy(alpha = 0.3f), CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "CIRCLE",
                    color = PrimaryGold,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 6.sp
                )
                Text(
                    text = "LOCK",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SENIN TERUS STUDIO",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "LOCKS OPENED: $totalCleared",
                        color = PrimaryGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        letterSpacing = 1.sp
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                color = SurfaceDark.copy(alpha = 0.92f),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuButton(
                        "PLAY",
                        onClick = onPlayClick,
                        gradient = GreenGradient
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuButton(
                            "SKINS",
                            onClick = onSkinsClick,
                            modifier = Modifier.weight(1f),
                            gradient = GoldGradient
                        )
                        MenuButton(
                            "QUESTS",
                            onClick = onQuestsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuButton(
                            "STREAK",
                            onClick = onStreakClick,
                            modifier = Modifier.weight(1f)
                        )
                        MenuButton(
                            "EVENTS",
                            onClick = onEventsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    MenuButton("HOW TO PLAY", onClick = onTutorialClick)
                    MenuButton("OPTIONS", onClick = onOptionsClick)
                    MenuButton("CREDITS", onClick = onCreditsClick)
                    MenuButton("EXIT", onClick = onExitClick)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
