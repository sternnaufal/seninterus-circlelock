package com.senintrerus.circlelock.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.engine.CircleLockGameEngine
import com.senintrerus.circlelock.engine.LevelGenerator
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.ui.components.GameStatusDialog
import com.senintrerus.circlelock.ui.theme.BackgroundDark
import com.senintrerus.circlelock.ui.theme.ErrorRed
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import com.senintrerus.circlelock.util.vibrateDevice
import com.senintrerus.circlelock.util.PlayerStats
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    level: Int, 
    mode: GameMode,
    unlockedLevel: Int,
    onLevelCleared: (Int) -> Unit,
    onNextLevel: () -> Unit,
    onBack: () -> Unit
) {
    var circles by remember(level, mode) {
        mutableStateOf(LevelGenerator.generateLevelCircles(level, mode))
    }
    var isWin by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    if (mode == GameMode.TIME_ATTACK && !isWin && !isGameOver) {
        LaunchedEffect(Unit) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isGameOver = true
        }
    }
    
    LaunchedEffect(circles) {
        val win = circles.all { 
            val normalized = (it.currentAngle % 360 + 360) % 360
            normalized < 5f || normalized > 355f
        }
        if (win && !isWin && !isGameOver) {
            isWin = true
            onLevelCleared(level)
            vibrateDevice(context)
            PlayerStats.incrementClearedCount(context)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LEVEL $level", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        if (mode == GameMode.TIME_ATTACK) {
                            Text(
                                "00:${timeLeft.toString().padStart(2, '0')}", 
                                color = if (timeLeft < 10) ErrorRed else PrimaryGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text(mode.getDisplayName(), color = PrimaryGold.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        circles = LevelGenerator.generateLevelCircles(level, mode)
                        timeLeft = 30
                        isGameOver = false
                        isWin = false 
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CircleLockGameEngine(
                circles = circles,
                onCirclesChanged = { circles = it },
                isWin = isWin || isGameOver,
                gameMode = mode
            )
            
            AnimatedVisibility(visible = isWin, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
                GameStatusDialog("SUCCESS!", PrimaryGold, onBack, onNextLevel)
            }
            
            AnimatedVisibility(visible = isGameOver, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
                GameStatusDialog("GAME OVER", ErrorRed, onBack, { 
                    isGameOver = false
                    timeLeft = 30
                    circles = LevelGenerator.generateLevelCircles(level, mode)
                }, "RETRY")
            }
        }
    }
}
