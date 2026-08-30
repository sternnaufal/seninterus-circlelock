@file:OptIn(ExperimentalFoundationApi::class)

package com.seninterus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.theme.*
import com.seninterus.circlelock.util.PlayerStats
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 4 })
    val totalPages = 4

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HOW TO PLAY", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> TutorialPageRotate()
                    1 -> TutorialPageAlign()
                    2 -> TutorialPageModes()
                    3 -> TutorialPageSkins()
                }
            }

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                repeat(totalPages) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryGold else Color.White.copy(alpha = 0.2f))
                    )
                }
            }

            // Action button
            Button(
                onClick = {
                    if (pagerState.currentPage < totalPages - 1) {
                        // This won't work with HorizontalPager directly, but we use the button as "Next" or "Done"
                    } else {
                        PlayerStats.setTutorialShown(context)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GoldGradient, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (pagerState.currentPage == totalPages - 1) "LET'S GO!" else "SWIPE TO CONTINUE",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialPageRotate() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val fingerX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fingerX"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated ring
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2f - 20f

                // Ring
                drawCircle(
                    color = PrimaryGold.copy(alpha = 0.3f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 20f)
                )

                // Gap indicator
                val gapAngle = Math.toRadians(rotation.toDouble())
                val gapX = center.x + (cos(gapAngle) * radius).toFloat()
                val gapY = center.y + (sin(gapAngle) * radius).toFloat()

                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = Offset(gapX, gapY)
                )

                // Reference line
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(center.x + radius - 30f, center.y),
                    end = Offset(center.x + radius + 30f, center.y),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }

            // Touch indicator
            Box(
                modifier = Modifier
                    .offset(
                        x = (fingerX * 60 - 30).dp,
                        y = (-40).dp
                    )
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f))
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "TOUCH & ROTATE",
            color = PrimaryGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Touch any ring and drag your finger in a circular motion to rotate it.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun TutorialPageAlign() {
    val infiniteTransition = rememberInfiniteTransition(label = "align")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alignProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val outerRadius = size.minDimension / 2f - 10f
                val innerRadius = outerRadius - 35f

                // Outer ring
                drawCircle(
                    color = PrimaryGold.copy(alpha = 0.3f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 16f)
                )

                // Inner ring
                drawCircle(
                    color = AccentBlue.copy(alpha = 0.3f),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 16f)
                )

                // Outer gap - animate toward 0 degrees
                val outerGapAngle = Math.toRadians((180f - animProgress * 180f).toDouble())
                val outerGapX = center.x + (cos(outerGapAngle) * outerRadius).toFloat()
                val outerGapY = center.y + (sin(outerGapAngle) * outerRadius).toFloat()
                drawCircle(color = Color.White, radius = 8f, center = Offset(outerGapX, outerGapY))

                // Inner gap - already aligned
                val innerGapX = center.x + outerRadius
                drawCircle(color = Color.White, radius = 8f, center = Offset(innerGapX, center.y))

                // Reference line
                drawLine(
                    color = SuccessGreen.copy(alpha = 0.6f),
                    start = Offset(center.x + outerRadius - 20f, center.y),
                    end = Offset(center.x + outerRadius + 20f, center.y),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "ALIGN THE GAPS",
            color = PrimaryGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Rotate each ring so the gap aligns with the reference line on the right.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gap is aligned!", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TutorialPageModes() {
    val modes = listOf(
        Triple("DARK", "Only your touch reveals the lock", "\uD83D\uDD76\uFE0F"),
        Triple("LINKED", "Rings move together", "\uD83D\uDD17"),
        Triple("SWITCH", "Control changes every 4s", "\uD83D\uDD04")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SPECIAL MODES",
            color = PrimaryGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        modes.forEach { (name, desc, icon) ->
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(icon, fontSize = 28.sp)
                    Column {
                        Text(name, color = PrimaryGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(desc, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Each mode adds a unique twist to the core mechanic.",
            color = TextDim,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TutorialPageSkins() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "CUSTOMIZE",
            color = PrimaryGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Earn locks by playing to unlock:",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Skin preview
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                Color(0xFFFFD700) to "SKINS",
                Color(0xFF00FFFF) to "12+"
            ).forEach { (color, label) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f))
                            .border(3.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            listOf(
                Color(0xFF7C4DFF) to "ANIM",
                Color(0xFF00E5FF) to "8+"
            ).forEach { (color, label) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f))
                            .border(3.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2728", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = SuccessGreen.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mix & match skins + animations freely!", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
