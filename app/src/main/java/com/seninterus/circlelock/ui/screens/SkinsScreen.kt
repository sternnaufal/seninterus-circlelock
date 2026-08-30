package com.seninterus.circlelock.ui.screens

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
import com.seninterus.circlelock.model.AnimationType
import com.seninterus.circlelock.model.SkinType
import com.seninterus.circlelock.ui.theme.*
import com.seninterus.circlelock.util.PlayerStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val totalCleared = remember { PlayerStats.getTotalClearedCount(context) }
    var currency by remember { mutableIntStateOf(PlayerStats.getCurrency(context)) }
    var activeSkin by remember { mutableStateOf(PlayerStats.getActiveSkin(context)) }
    var activeAnim by remember { mutableStateOf(PlayerStats.getActiveAnim(context)) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SHOP", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            // Currency + Stats
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                color = SurfaceDark,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LOCKS OPENED", color = TextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$totalCleared", color = PrimaryGold, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BALANCE", color = TextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$currency", color = SuccessGreen, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = PrimaryGold,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("SKINS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("ANIMATIONS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            when (selectedTab) {
                0 -> SkinsGrid(
                    currency = currency,
                    activeSkin = activeSkin,
                    onSelectSkin = {
                        PlayerStats.setActiveSkin(context, it)
                        activeSkin = it
                    },
                    onUnlockSkin = { skin ->
                        if (PlayerStats.spendCurrency(context, skin.cost)) {
                            currency = PlayerStats.getCurrency(context)
                            PlayerStats.unlockSkin(context, skin.name)
                            PlayerStats.setActiveSkin(context, skin.name)
                            activeSkin = skin.name
                        }
                    }
                )
                1 -> AnimationsGrid(
                    currency = currency,
                    activeAnim = activeAnim,
                    onSelectAnim = {
                        PlayerStats.setActiveAnim(context, it)
                        activeAnim = it
                    },
                    onUnlockAnim = { anim ->
                        if (PlayerStats.spendCurrency(context, anim.cost)) {
                            currency = PlayerStats.getCurrency(context)
                            PlayerStats.unlockAnim(context, anim.name)
                            PlayerStats.setActiveAnim(context, anim.name)
                            activeAnim = anim.name
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SkinsGrid(
    currency: Int,
    activeSkin: String,
    onSelectSkin: (String) -> Unit,
    onUnlockSkin: (SkinType) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(SkinType.entries) { skin ->
            SkinItem(
                skin = skin,
                isActive = activeSkin == skin.name,
                isUnlocked = PlayerStats.isSkinUnlocked(
                    androidx.compose.ui.platform.LocalContext.current,
                    skin.name
                ),
                canAfford = currency >= skin.cost,
                onSelect = { onSelectSkin(skin.name) },
                onUnlock = { onUnlockSkin(skin) }
            )
        }
    }
}

@Composable
private fun AnimationsGrid(
    currency: Int,
    activeAnim: String,
    onSelectAnim: (String) -> Unit,
    onUnlockAnim: (AnimationType) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(AnimationType.entries) { anim ->
            AnimationItem(
                anim = anim,
                isActive = activeAnim == anim.name,
                isUnlocked = PlayerStats.isAnimUnlocked(
                    androidx.compose.ui.platform.LocalContext.current,
                    anim.name
                ),
                canAfford = currency >= anim.cost,
                onSelect = { onSelectAnim(anim.name) },
                onUnlock = { onUnlockAnim(anim) }
            )
        }
    }
}

@Composable
private fun SkinItem(
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
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked || canAfford) {
                if (isUnlocked) onSelect() else onUnlock()
            }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.sweepGradient(skin.colors))
                .border(3.dp, Color.Black.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            } else if (isActive) {
                Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(skin.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(4.dp))

        if (isUnlocked) {
            Surface(
                color = if (isActive) SuccessGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    if (isActive) "ACTIVE" else "SELECT",
                    color = if (isActive) SuccessGreen else TextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${skin.cost}", color = if (canAfford) PrimaryGold else TextDim, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text("LOCKS", color = TextDim, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnimationItem(
    anim: AnimationType,
    isActive: Boolean,
    isUnlocked: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val borderColor = when {
        isActive -> SuccessGreen
        isUnlocked -> AccentPurple.copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.06f)
    }

    val icon = when (anim) {
        AnimationType.CLASSIC -> "\uD83C\uDF86"
        AnimationType.CONFETTI -> "\uD83C\uDF8A"
        AnimationType.SPARKLE -> "\u2728"
        AnimationType.SHOCKWAVE -> "\uD83D\uDCA5"
        AnimationType.FIRE -> "\uD83D\uDD25"
        AnimationType.SNOWFLAKE -> "\u2744\uFE0F"
        AnimationType.GALAXY -> "\uD83C\uDF0C"
        AnimationType.MATRIX -> "\uD83D\uDCDD"
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked || canAfford) {
                if (isUnlocked) onSelect() else onUnlock()
            }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(AccentPurple.copy(alpha = 0.1f))
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            } else {
                Text(icon, fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(anim.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(2.dp))

        Text(anim.description, color = TextDim, fontSize = 7.sp, textAlign = TextAlign.Center, maxLines = 1)

        Spacer(modifier = Modifier.height(4.dp))

        if (isUnlocked) {
            Surface(
                color = if (isActive) SuccessGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    if (isActive) "ACTIVE" else "SELECT",
                    color = if (isActive) SuccessGreen else TextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${anim.cost}", color = if (canAfford) AccentPurple else TextDim, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text("LOCKS", color = TextDim, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
