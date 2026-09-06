package com.seninterus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.components.BannerAd
import com.seninterus.circlelock.ui.theme.*
import com.seninterus.circlelock.util.PlayerStats

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
        initialValue = 0.02f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PrimaryGold.copy(alpha = glowAlpha), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 1.5f
                ),
                radius = size.minDimension / 2f * glowScale
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(glowScale)
                            .blur(16.dp)
                            .clip(CircleShape)
                            .background(PrimaryGold.copy(alpha = 0.08f))
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(glowScale)
                            .clip(CircleShape)
                            .border(2.dp, PrimaryGold.copy(alpha = 0.4f), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CIRCLE",
                    color = PrimaryGold,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 8.sp
                )
                Text(
                    text = "LOCK",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "SENIN TERUS STUDIO",
                    color = TextDim,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "LOCKS OPENED: $totalCleared",
                    color = PrimaryGold.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreenGradient),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = onPlayClick,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                    ) {
                        Text(
                            "PLAY",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientMenuButton("SKINS", onClick = onSkinsClick, modifier = Modifier.weight(1f).height(48.dp), gradient = GoldGradient)
                    GradientMenuButton("QUESTS", onClick = onQuestsClick, modifier = Modifier.weight(1f).height(48.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GradientMenuButton("STREAK", onClick = onStreakClick, modifier = Modifier.weight(1f).height(48.dp))
                    GradientMenuButton("EVENTS", onClick = onEventsClick, modifier = Modifier.weight(1f).height(48.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SmallIconButton("HOW TO PLAY", onClick = onTutorialClick, modifier = Modifier.weight(1f))
                    SmallIconButton("OPTIONS", onClick = onOptionsClick, modifier = Modifier.weight(1f))
                    SmallIconButton("CREDITS", onClick = onCreditsClick, modifier = Modifier.weight(1f))
                }

                TextButton(
                    onClick = onExitClick,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        "EXIT",
                        color = TextDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            BannerAd(modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}

@Composable
private fun GradientMenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "btnScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (gradient != null) Modifier.background(gradient)
                else Modifier.background(SurfaceLight)
            )
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Text(
                text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun SmallIconButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "smallBtnScale"
    )

    Box(
        modifier = modifier
            .height(40.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
        ) {
            Text(
                text,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
