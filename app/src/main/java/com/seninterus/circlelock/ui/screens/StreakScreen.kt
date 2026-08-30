package com.seninterus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.components.MenuButton
import com.seninterus.circlelock.ui.theme.*
import com.seninterus.circlelock.util.PlayerStats
import com.seninterus.circlelock.util.ShareManager
import com.seninterus.circlelock.util.StreakManager
import com.seninterus.circlelock.util.DayReward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var streakInfo by remember { mutableStateOf(StreakManager.getStreakInfo(context)) }
    var showClaimDialog by remember { mutableStateOf(false) }
    var claimedAmount by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "streak")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fireScale"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "DAILY STREAK",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        ShareManager.shareStreakResult(context, streakInfo.currentStreak, streakInfo.todayReward)
                    }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Share",
                            tint = PrimaryGold
                        )
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Streak fire icon
            Text(
                text = "\uD83D\uDD25",
                fontSize = 80.sp,
                modifier = Modifier.scale(fireScale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Streak count
            Text(
                text = "${streakInfo.currentStreak}",
                color = PrimaryGold,
                fontSize = 72.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "DAY STREAK",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highest streak
            Text(
                text = "Best: ${streakInfo.highestStreak} days",
                color = TextDim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Reward calendar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark.copy(alpha = 0.92f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WEEKLY REWARDS",
                        color = PrimaryGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        streakInfo.rewards.forEachIndexed { index, reward ->
                            RewardDayItem(
                                reward = reward,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Claim button
                    if (!streakInfo.hasClaimedToday) {
                        MenuButton(
                            "CLAIM +${streakInfo.todayReward} LOCKS",
                            onClick = {
                                if (StreakManager.claimDailyReward(context)) {
                                    claimedAmount = streakInfo.todayReward
                                    showClaimDialog = true
                                    streakInfo = StreakManager.getStreakInfo(context)
                                }
                            },
                            gradient = GoldGradient
                        )
                    } else {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "CLAIMED TODAY",
                                        color = SuccessGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Currency display
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uD83D\uDCB0",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${PlayerStats.getCurrency(context)} Locks",
                        color = PrimaryGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showClaimDialog) {
        ClaimRewardDialog(
            amount = claimedAmount,
            streak = streakInfo.currentStreak,
            onDismiss = { showClaimDialog = false },
            onShare = {
                ShareManager.shareStreakResult(context, streakInfo.currentStreak, claimedAmount)
                showClaimDialog = false
            }
        )
    }
}

@Composable
private fun RewardDayItem(reward: DayReward, modifier: Modifier = Modifier) {
    val borderColor = when {
        reward.isClaimed -> SuccessGreen
        reward.isToday -> PrimaryGold
        reward.isFuture -> Color.White.copy(alpha = 0.08f)
        else -> Color.White.copy(alpha = 0.12f)
    }

    val bgColor = when {
        reward.isClaimed -> SuccessGreen.copy(alpha = 0.15f)
        reward.isToday -> PrimaryGold.copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (reward.isClaimed) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Claimed",
                    tint = SuccessGreen,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = "+${reward.reward}",
                    color = if (reward.isToday) PrimaryGold else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "DAY ${reward.day}",
            color = if (reward.isToday) PrimaryGold else TextDim,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ClaimRewardDialog(
    amount: Int,
    streak: Int,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        titleContentColor = PrimaryGold,
        textContentColor = Color.White,
        title = {
            Text(
                "DAILY REWARD!",
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "+$amount",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryGold
                )
                Text(
                    text = "LOCKS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Day $streak streak!",
                    fontSize = 14.sp,
                    color = SuccessGreen
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.3f))
                ) {
                    Text("SHARE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                }
            }
        },
        dismissButton = null
    )
}
