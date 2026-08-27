package com.senintrerus.circlelock.ui.screens

import androidx.activity.compose.BackHandler
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
import com.senintrerus.circlelock.model.AnimationType
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.model.SkinType
import com.senintrerus.circlelock.ui.components.GameStatusDialog
import com.senintrerus.circlelock.ui.components.WinAnimation
import com.senintrerus.circlelock.ui.theme.BackgroundDark
import com.senintrerus.circlelock.ui.theme.ErrorRed
import com.senintrerus.circlelock.ui.theme.PrimaryGold
import com.senintrerus.circlelock.util.AudioManager
import com.senintrerus.circlelock.util.PlayerStats
import com.senintrerus.circlelock.util.QuestManager
import com.senintrerus.circlelock.util.ShareManager
import com.senintrerus.circlelock.util.EventManager
import com.senintrerus.circlelock.util.vibrateDevice
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    level: Int,
    mode: GameMode,
    onLevelCleared: (Int) -> Unit,
    onNextLevel: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeSkinName = remember { PlayerStats.getActiveSkin(context) }
    val activeSkin = remember(activeSkinName) { SkinType.valueOf(activeSkinName) }
    val activeAnimName = remember { PlayerStats.getActiveAnim(context) }
    val activeAnim = remember(activeAnimName) { runCatching { AnimationType.valueOf(activeAnimName) }.getOrDefault(AnimationType.CLASSIC) }
    val totalCleared = remember { PlayerStats.getTotalClearedCount(context) }

    var circles by remember(level, mode, activeSkin) {
        mutableStateOf(LevelGenerator.generateLevelCircles(level, mode, activeSkin))
    }
    var isWin by remember(level, mode) { mutableStateOf(false) }
    var isGameOver by remember(level, mode) { mutableStateOf(false) }
    var timeLeft by remember(level, mode) { mutableIntStateOf(if (mode == GameMode.ENDLESS) 60 else 30) }
    var score by remember(level, mode) { mutableIntStateOf(0) }
    var switchTargetId by remember(level, mode) { mutableIntStateOf(0) }

    // Back button → navigate back, not close app
    BackHandler { onBack() }

    if (mode == GameMode.SWITCH && !isWin && !isGameOver) {
        LaunchedEffect(circles.size, isWin, isGameOver) {
            while (circles.isNotEmpty() && !isWin && !isGameOver) {
                delay(4000)
                switchTargetId = (switchTargetId + 1) % circles.size
            }
        }
    }

    if ((mode == GameMode.TIME_ATTACK || mode == GameMode.ENDLESS) && !isWin && !isGameOver) {
        LaunchedEffect(timeLeft, isWin, isGameOver, mode) {
            while (timeLeft > 0 && !isWin && !isGameOver) {
                delay(1000)
                timeLeft--
            }
            if (!isWin && timeLeft <= 0) {
                isGameOver = true
            }
        }
    }

    LaunchedEffect(circles) {
        if (circles.isEmpty()) return@LaunchedEffect

        val win = circles.all {
            val normalized = (it.currentAngle % 360 + 360) % 360
            normalized < 5f || normalized > 355f
        }
        if (win && !isWin && !isGameOver) {
            if (mode == GameMode.ENDLESS) {
                score++
                timeLeft += 2
                vibrateDevice(context)
                AudioManager.playSound(context, "snap")
                PlayerStats.incrementClearedCount(context)
                PlayerStats.addCurrency(context, 2)
                QuestManager.updateProgress(context, "Locks")
                EventManager.updateProgress(context, mode, score)
                delay(500)
                circles = LevelGenerator.generateLevelCircles(level + (score / 2), mode, activeSkin)
                switchTargetId = 0
            } else {
                isWin = true
                onLevelCleared(level)
                vibrateDevice(context)
                AudioManager.playSound(context, "win")
                PlayerStats.incrementClearedCount(context)
                val currencyReward = if (mode == GameMode.TIME_ATTACK) 3 else 2
                PlayerStats.addCurrency(context, currencyReward)

                QuestManager.updateProgress(context, "Locks")
                if (mode == GameMode.TIME_ATTACK) QuestManager.updateProgress(context, "Time Attack")
            }
            QuestManager.updateProgress(context, "Play")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (mode == GameMode.ENDLESS) "SCORE: $score" else "LEVEL $level", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        if (mode == GameMode.TIME_ATTACK || mode == GameMode.ENDLESS) {
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
                        circles = LevelGenerator.generateLevelCircles(level, mode, activeSkin)
                        isWin = false
                        isGameOver = false
                        timeLeft = if (mode == GameMode.ENDLESS) 60 else 30
                        score = 0
                        switchTargetId = 0
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
                onGameOver = {
                    isGameOver = true
                    AudioManager.playSound(context, "error")
                },
                isWin = isWin || isGameOver,
                gameMode = mode,
                switchTargetId = switchTargetId
            )

            WinAnimation(isVisible = isWin, style = activeAnim, skinColors = activeSkin.colors)

            AnimatedVisibility(visible = isWin, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
                GameStatusDialog(
                    "SUCCESS!",
                    PrimaryGold,
                    onBack,
                    onNextLevel,
                    onShare = {
                        ShareManager.shareWinResult(context, level, mode, totalCleared)
                    }
                )
            }

            AnimatedVisibility(visible = isGameOver, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
                GameStatusDialog(
                    if (mode == GameMode.ENDLESS) "FINAL SCORE: $score" else "GAME OVER",
                    ErrorRed,
                    onBack,
                    {
                        circles = LevelGenerator.generateLevelCircles(level, mode, activeSkin)
                        isWin = false
                        isGameOver = false
                        timeLeft = if (mode == GameMode.ENDLESS) 60 else 30
                        score = 0
                        switchTargetId = 0
                    },
                    "RETRY"
                )
            }
        }
    }
}
