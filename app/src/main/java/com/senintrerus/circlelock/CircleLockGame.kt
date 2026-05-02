package com.senintrerus.circlelock

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

// --- Models ---

data class CircleData(
    val id: Int,
    val radius: Float,
    var currentAngle: Float,
    val gapWidth: Float = 45f,
    val color: Color = Color.Gray,
    val rotationSpeed: Float = 1f // Difficulty factor
)

enum class GameMode {
    STANDARD, DARK, TIME_ATTACK
}

sealed class Screen {
    object Main : Screen()
    object LevelSelect : Screen()
    data class Game(val level: Int, val mode: GameMode) : Screen()
    object Options : Screen()
    object Credits : Screen()
}

// --- App State & Navigation ---

@Composable
fun CircleLockApp() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("circle_lock_prefs", Context.MODE_PRIVATE) }
    var unlockedLevel by remember { mutableStateOf(sharedPrefs.getInt("unlocked_level", 1)) }
    var selectedMode by remember { mutableStateOf(GameMode.STANDARD) }
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    val activity = context as? Activity

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        when (val screen = currentScreen) {
            is Screen.Main -> MainScreen(
                onPlayClick = { currentScreen = Screen.LevelSelect },
                onOptionsClick = { currentScreen = Screen.Options },
                onCreditsClick = { currentScreen = Screen.Credits },
                onExitClick = { activity?.finish() }
            )
            is Screen.LevelSelect -> LevelSelectScreen(
                unlockedLevel = unlockedLevel,
                selectedMode = selectedMode,
                onModeChanged = { selectedMode = it },
                onLevelSelected = { currentScreen = Screen.Game(it, selectedMode) },
                onBack = { currentScreen = Screen.Main }
            )
            is Screen.Game -> GameScreen(
                level = screen.level,
                mode = screen.mode,
                unlockedLevel = unlockedLevel,
                onLevelCleared = { level ->
                    if (level >= unlockedLevel) {
                        unlockedLevel = level + 1
                        sharedPrefs.edit().putInt("unlocked_level", unlockedLevel).apply()
                    }
                },
                onNextLevel = { currentScreen = Screen.Game(screen.level + 1, screen.mode) },
                onBack = { currentScreen = Screen.LevelSelect }
            )
            is Screen.Options -> OptionsScreen(
                onBack = { currentScreen = Screen.Main }
            )
            is Screen.Credits -> CreditsScreen(
                onBack = { currentScreen = Screen.Main }
            )
        }
    }
}

// --- Screens ---

@Composable
fun MainScreen(
    onPlayClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CIRCLE LOCK",
            color = Color(0xFFFFD700),
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "SENIN TERUS STUDIO",
            color = Color.Gray,
            fontSize = 14.sp,
            letterSpacing = 4.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        MenuButton("PLAY", onClick = onPlayClick, containerColor = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton("OPTIONS", onClick = onOptionsClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton("CREDITS", onClick = onCreditsClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton("EXIT", onClick = onExitClick, containerColor = Color(0xFFB00020))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    unlockedLevel: Int, 
    selectedMode: GameMode,
    onModeChanged: (GameMode) -> Unit,
    onLevelSelected: (Int) -> Unit, 
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Level", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameMode.values().forEach { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { onModeChanged(mode) },
                        label = { Text(mode.name.replace("_", " ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = Color.Gray,
                            selectedLabelColor = Color.White,
                            selectedContainerColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(20) { index ->
                    val level = index + 1
                    val isUnlocked = level <= unlockedLevel
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUnlocked) Color(0xFF1E1E1E) else Color(0xFF121212))
                            .then(if (isUnlocked) Modifier.clickable { onLevelSelected(level) } else Modifier)
                            .border(
                                width = 1.dp, 
                                color = if (isUnlocked) Color.Gray.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(
                                text = level.toString(),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Settings, // Placeholder for lock icon
                                contentDescription = "Locked",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

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
    var circles by remember(level) {
        mutableStateOf(generateLevelCircles(level))
    }
    var isWin by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    // Time Attack logic
    if (mode == GameMode.TIME_ATTACK && !isWin && !isGameOver) {
        LaunchedEffect(Unit) {
            while (timeLeft > 0) {
                kotlinx.coroutines.delay(1000)
                timeLeft--
            }
            isGameOver = true
        }
    }
    
    // Logic check win
    LaunchedEffect(circles) {
        val win = circles.all { 
            val normalized = (it.currentAngle % 360 + 360) % 360
            normalized < 5f || normalized > 355f
        }
        if (win && !isWin && !isGameOver) {
            isWin = true
            onLevelCleared(level)
            vibrateDevice(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Level $level", color = Color.White, fontSize = 18.sp)
                        if (mode == GameMode.TIME_ATTACK) {
                            Text("Waktu: $timeLeft", color = if (timeLeft < 10) Color.Red else Color.Yellow, fontSize = 14.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CircleLockGameEngine(
                circles = circles,
                onCirclesChanged = { circles = it },
                isWin = isWin || isGameOver,
                gameMode = mode
            )
            
            if (isWin) {
                GameStatusDialog("BERHASIL!", Color(0xFFFFD700), onBack, onNextLevel)
            } else if (isGameOver) {
                GameStatusDialog("WAKTU HABIS!", Color.Red, onBack, { 
                    isGameOver = false
                    timeLeft = 30
                    circles = generateLevelCircles(level)
                }, "RETRY")
            }
        }
    }
}

@Composable
fun GameStatusDialog(title: String, titleColor: Color, onBack: () -> Unit, onAction: () -> Unit, actionText: String = "NEXT LEVEL") {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = titleColor, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text("MENU")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(actionText)
                }
            }
        }
    }
}

fun vibrateDevice(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opsi", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp)) {
            Text("Suara", color = Color.White, fontSize = 18.sp)
            Slider(value = 0.5f, onValueChange = {})
            Spacer(modifier = Modifier.height(16.dp))
            Text("Getaran", color = Color.White, fontSize = 18.sp)
            Switch(checked = true, onCheckedChange = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kredit", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Dibuat oleh:", color = Color.Gray, fontSize = 16.sp)
            Text("Senin Terus Studio", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Terima kasih telah bermain!", color = Color.LightGray, textAlign = TextAlign.Center)
        }
    }
}

// --- Components ---

@Composable
fun MenuButton(text: String, onClick: () -> Unit, containerColor: Color = Color(0xFF1E1E1E)) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CircleLockGameEngine(
    circles: List<CircleData>,
    onCirclesChanged: (List<CircleData>) -> Unit,
    isWin: Boolean,
    gameMode: GameMode = GameMode.STANDARD
) {
    var activeCircleId by remember { mutableStateOf<Int?>(null) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(350.dp)
                .pointerInput(isWin) {
                    if (isWin) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val dist = sqrt((offset.x - centerX) * (offset.x - centerX) + (offset.y - centerY) * (offset.y - centerY))
                            activeCircleId = circles.minByOrNull { kotlin.math.abs(it.radius * 2.2f - dist) }?.id
                            touchPosition = offset
                        },
                        onDragEnd = { 
                            onCirclesChanged(circles.map {
                                val normalized = (it.currentAngle % 360 + 360) % 360
                                if (normalized < 8f || normalized > 352f) it.copy(currentAngle = 0f) else it
                            })
                            activeCircleId = null 
                            touchPosition = null
                        },
                        onDragCancel = { 
                            activeCircleId = null 
                            touchPosition = null
                        }
                    ) { change, _ ->
                        touchPosition = change.position
                        if (activeCircleId != null) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val oldPos = change.previousPosition
                            val newPos = change.position
                            val oldAngle = Math.toDegrees(atan2(oldPos.y - centerY, oldPos.x - centerX).toDouble()).toFloat()
                            val newAngle = Math.toDegrees(atan2(newPos.y - centerY, newPos.x - centerX).toDouble()).toFloat()
                            
                            var diff = (newAngle - oldAngle)
                            if (diff > 180) diff -= 360
                            if (diff < -180) diff += 360
                            
                            val activeCircle = circles.find { it.id == activeCircleId }
                            val actualDiff = diff * (activeCircle?.rotationSpeed ?: 1f)

                            onCirclesChanged(circles.map {
                                if (it.id == activeCircleId) {
                                    it.copy(currentAngle = (it.currentAngle + actualDiff) % 360)
                                } else {
                                    it
                                }
                            })
                        }
                    }
                }
        ) {
            val center = this.center
            
            // Draw Target Line
            drawLine(
                color = if (isWin) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.2f),
                start = center,
                end = Offset(center.x + 180.dp.toPx(), center.y),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            circles.forEach { circle ->
                val normalizedAngle = (circle.currentAngle % 360 + 360) % 360
                val isAligned = normalizedAngle < 8f || normalizedAngle > 352f
                val isActive = circle.id == activeCircleId
                
                val color = when {
                    isAligned -> Color(0xFF4CAF50)
                    isActive -> circle.color
                    else -> circle.color.copy(alpha = 0.5f)
                }
                
                val strokeWidth = if (isActive) 20.dp.toPx() else 16.dp.toPx()

                drawArc(
                    color = color,
                    startAngle = circle.currentAngle + (circle.gapWidth / 2f),
                    sweepAngle = 360f - circle.gapWidth,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(circle.radius * 4.4f, circle.radius * 4.4f),
                    topLeft = Offset(center.x - circle.radius * 2.2f, center.y - circle.radius * 2.2f)
                )
            }

            // Dark Mode / Flashlight Overlay
            if (gameMode == GameMode.DARK && !isWin) {
                val touch = touchPosition
                if (touch != null) {
                    drawRect(
                        brush = Brush.radialGradient(
                            0.0f to Color.Transparent,
                            0.8f to Color.Black.copy(alpha = 0.95f),
                            center = touch,
                            radius = 100.dp.toPx()
                        ),
                        size = size
                    )
                } else {
                    drawRect(color = Color.Black.copy(alpha = 0.95f), size = size)
                }
            }
        }
    }
}

// --- Level Generator ---

private fun generateLevelCircles(level: Int): List<CircleData> {
    val count = when {
        level <= 5 -> 2
        level <= 12 -> 3
        else -> 4
    }
    
    val colors = listOf(Color(0xFFCD7F32), Color(0xFFC0C0C0), Color(0xFFFFD700), Color(0xFFE5E4E2))
    
    return List(count) { i ->
        CircleData(
            id = i,
            radius = 45f + (i * 35f),
            currentAngle = Random.nextInt(360).toFloat(),
            color = colors.getOrElse(i) { Color.Cyan },
            rotationSpeed = 1f - (level * 0.02f).coerceAtMost(0.5f) // Gets harder to rotate precisely
        )
    }
}

@Preview
@Composable
fun PreviewApp() {
    CircleLockApp()
}
