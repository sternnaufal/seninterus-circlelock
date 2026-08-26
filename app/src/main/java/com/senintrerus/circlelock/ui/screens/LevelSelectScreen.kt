package com.senintrerus.circlelock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.model.GameMode
import com.senintrerus.circlelock.ui.components.LevelCard
import com.senintrerus.circlelock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    unlockedLevel: Int,
    mode: GameMode,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SELECT LEVEL", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text(mode.getDisplayName(), color = PrimaryGold.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(24) { index ->
                    val level = index + 1
                    val isUnlocked = level <= unlockedLevel
                    val isCompleted = level < unlockedLevel
                    val isCurrent = level == unlockedLevel

                    LevelCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        onClick = { onLevelSelected(level) }
                    )
                }
            }
        }
    }
}
