package com.senintrerus.circlelock

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.model.Screen
import com.senintrerus.circlelock.ui.screens.*
import com.senintrerus.circlelock.ui.theme.BackgroundDark

@Composable
fun CircleLockApp() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("circle_lock_prefs", Context.MODE_PRIVATE) }
    // Track unlocked levels for each mode separately
    val unlockedLevels = remember { mutableStateMapOf<GameMode, Int>() }
    
    LaunchedEffect(Unit) {
        GameMode.entries.forEach { mode ->
            unlockedLevels[mode] = sharedPrefs.getInt("unlocked_level_${mode.name}", 1)
        }
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    val activity = context as? Activity

    // Save/restore last selected mode
    var lastMode by remember {
        val saved = sharedPrefs.getString("last_mode", null)
        mutableStateOf(saved?.let { runCatching { GameMode.valueOf(it) }.getOrNull() } ?: GameMode.STANDARD)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                is Screen.Main -> MainScreen(
                    onPlayClick = { currentScreen = Screen.ModeSelect },
                    onSkinsClick = { currentScreen = Screen.Skins },
                    onTutorialClick = { currentScreen = Screen.Tutorial },
                    onQuestsClick = { currentScreen = Screen.Quests },
                    onOptionsClick = { currentScreen = Screen.Options },
                    onCreditsClick = { currentScreen = Screen.Credits },
                    onExitClick = { activity?.moveTaskToBack(true) }
                )
                is Screen.Skins -> SkinsScreen(
                    onBack = { currentScreen = Screen.Main }
                )
                is Screen.Tutorial -> TutorialScreen(
                    onBack = { currentScreen = Screen.Main }
                )
                is Screen.Quests -> QuestsScreen(
                    onBack = { currentScreen = Screen.Main }
                )
                is Screen.ModeSelect -> ModeSelectScreen(
                    initialMode = lastMode,
                    onModeSelected = { mode ->
                        lastMode = mode
                        sharedPrefs.edit().putString("last_mode", mode.name).apply()
                        currentScreen = Screen.LevelSelect(mode)
                    },
                    onBack = { currentScreen = Screen.Main }
                )
                is Screen.LevelSelect -> LevelSelectScreen(
                    unlockedLevel = unlockedLevels[screen.mode] ?: 1,
                    mode = screen.mode,
                    onLevelSelected = { currentScreen = Screen.Game(it, screen.mode) },
                    onBack = { currentScreen = Screen.ModeSelect }
                )
                is Screen.Game -> GameScreen(
                    level = screen.level,
                    mode = screen.mode,
                    unlockedLevel = unlockedLevels[screen.mode] ?: 1,
                    onLevelCleared = { level ->
                        val currentMax = unlockedLevels[screen.mode] ?: 1
                        if (level >= currentMax) {
                            val nextLevel = level + 1
                            unlockedLevels[screen.mode] = nextLevel
                            sharedPrefs.edit().putInt("unlocked_level_${screen.mode.name}", nextLevel).apply()
                        }
                    },
                    onNextLevel = { currentScreen = Screen.Game(screen.level + 1, screen.mode) },
                    onBack = { currentScreen = Screen.LevelSelect(screen.mode) }
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
}
