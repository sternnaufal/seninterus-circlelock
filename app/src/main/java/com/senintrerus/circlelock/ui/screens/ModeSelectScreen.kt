package com.senintrerus.circlelock.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    onModeSelected: (GameMode) -> Unit,
    onBack: () -> Unit
) {
    val modes = GameMode.entries.toTypedArray()
    val pagerState = rememberPagerState(pageCount = { modes.size })

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
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp,
                modifier = Modifier.fillMaxWidth().height(400.dp)
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
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Pager Indicators
            Row(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(modes.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val color = if (isSelected) PrimaryGold else Color.Gray.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
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
    val icon = when (mode) {
        GameMode.STANDARD -> Icons.Default.Lock
        GameMode.DARK -> Icons.Default.Star
        GameMode.TIME_ATTACK -> Icons.Default.Refresh
        GameMode.LINKED -> Icons.Default.Share
    }

    val cardColor = when (mode) {
        GameMode.LINKED -> PrimaryGold
        else -> SurfaceDark
    }
    
    val contentColor = when (mode) {
        GameMode.LINKED -> BackgroundDark
        else -> Color.White
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (mode == GameMode.LINKED) BackgroundDark else PrimaryGold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = mode.getDisplayName(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = contentColor,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = mode.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = if (mode == GameMode.LINKED) BackgroundDark else SuccessGreen,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "PLAY NOW",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
