package com.senintrerus.circlelock.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.ui.theme.*
import kotlin.math.absoluteValue

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ModeSelectScreen(
    initialMode: GameMode = GameMode.STANDARD,
    onModeSelected: (GameMode) -> Unit,
    onBack: () -> Unit
) {
    val modes = GameMode.entries.toTypedArray()
    val initialPage = modes.indexOf(initialMode).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { modes.size })

    LaunchedEffect(Unit) {
        pagerState.animateScrollToPage(initialPage)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CHOOSE MODE", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 40.dp),
                pageSpacing = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) { page ->
                val mode = modes[page]
                ModeCard(
                    mode = mode,
                    onClick = { onModeSelected(mode) },
                    modifier = Modifier
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue
                            alpha = lerp(
                                start = 0.4f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = lerp(
                                start = 0.82f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                Modifier
                    .height(6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(modes.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val color = if (isSelected) PrimaryGold else Color.Gray.copy(alpha = 0.2f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                            .size(if (isSelected) 20.dp else 6.dp, 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModeCard(
    mode: GameMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "modePress"
    )

    val icon: ImageVector = when (mode) {
        GameMode.STANDARD -> Icons.Default.Lock
        GameMode.DARK -> Icons.Default.Star
        GameMode.TIME_ATTACK -> Icons.Default.Refresh
        GameMode.LINKED -> Icons.Default.Share
        GameMode.SWITCH -> Icons.Default.Notifications
        GameMode.ENDLESS -> Icons.Default.PlayArrow
    }

    val (cardGradient, contentColor) = when (mode) {
        GameMode.STANDARD -> Brush.verticalGradient(listOf(SurfaceLight, SurfaceDark)) to Color.White
        GameMode.DARK -> Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF0F0F1A))) to Color.White
        GameMode.TIME_ATTACK -> Brush.verticalGradient(listOf(Color(0xFFCF6679), Color(0xFF8B3A4A))) to Color.White
        GameMode.LINKED -> Brush.verticalGradient(listOf(PrimaryGold, SecondaryGold)) to BackgroundDark
        GameMode.SWITCH -> Brush.verticalGradient(listOf(AccentBlue, Color(0xFF1565C0))) to Color.White
        GameMode.ENDLESS -> Brush.verticalGradient(listOf(SuccessGreen, Color(0xFF2E7D32))) to Color.White
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .scale(pressScale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.15f))
                        .border(2.dp, contentColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = contentColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = mode.getDisplayName(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = contentColor,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = mode.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Surface(
                    color = contentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "PLAY NOW",
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = contentColor,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
