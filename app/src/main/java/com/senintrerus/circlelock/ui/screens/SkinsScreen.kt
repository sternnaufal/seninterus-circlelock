package com.senintrerus.circlelock.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.model.SkinType
import com.senintrerus.circlelock.ui.theme.*
import com.senintrerus.circlelock.util.PlayerStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val totalCleared = remember { PlayerStats.getTotalClearedCount(context) }
    var activeSkin by remember { mutableStateOf(PlayerStats.getActiveSkin(context)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SKIN SHOP", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                color = SurfaceDark,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LOCKS OPENED", color = TextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$totalCleared", color = PrimaryGold, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    }
                    Surface(
                        color = PrimaryGold.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "USE LOCKS TO\nUNLOCK SKINS",
                            color = PrimaryGold.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(SkinType.entries) { skin ->
                    SkinItem(
                        skin = skin,
                        isActive = activeSkin == skin.name,
                        isUnlocked = PlayerStats.isSkinUnlocked(context, skin.name),
                        canAfford = totalCleared >= skin.cost,
                        onSelect = {
                            PlayerStats.setActiveSkin(context, skin.name)
                            activeSkin = skin.name
                        },
                        onUnlock = {
                            PlayerStats.unlockSkin(context, skin.name)
                            PlayerStats.setActiveSkin(context, skin.name)
                            activeSkin = skin.name
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SkinItem(
    skin: SkinType,
    isActive: Boolean,
    isUnlocked: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val borderColor = when {
        isActive -> SuccessGreen
        isUnlocked -> PrimaryGold.copy(alpha = 0.25f)
        else -> Color.White.copy(alpha = 0.06f)
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(enabled = isUnlocked || canAfford) {
                if (isUnlocked) onSelect() else onUnlock()
            }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.sweepGradient(skin.colors))
                .border(3.dp, Color.Black.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            } else if (isActive) {
                Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(skin.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(6.dp))

        if (isUnlocked) {
            Surface(
                color = if (isActive) SuccessGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    if (isActive) "ACTIVE" else "SELECT",
                    color = if (isActive) SuccessGreen else TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${skin.cost}", color = if (canAfford) PrimaryGold else TextDim, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("LOCKS", color = TextDim, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
