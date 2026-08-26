package com.senintrerus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.model.DailyQuest
import com.senintrerus.circlelock.ui.theme.*
import com.senintrerus.circlelock.util.QuestManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var quests by remember { mutableStateOf(QuestManager.getDailyQuests(context)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DAILY QUESTS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Surface(
                color = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.1f))
            ) {
                Text(
                    "Complete tasks daily to earn rewards!",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(quests) { index, quest ->
                    QuestItem(
                        quest = quest,
                        onClaim = {
                            if (QuestManager.claimReward(context, index)) {
                                quests = QuestManager.getDailyQuests(context)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestItem(quest: DailyQuest, onClaim: () -> Unit) {
    val progress = (quest.current.toFloat() / quest.target.toFloat()).coerceIn(0f, 1f)
    val isComplete = quest.current >= quest.target

    val infiniteTransition = rememberInfiniteTransition(label = "questGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val borderColor = when {
        quest.isClaimed -> Color.Gray.copy(alpha = 0.15f)
        isComplete -> SuccessGreen.copy(alpha = glowAlpha)
        else -> Color.White.copy(alpha = 0.06f)
    }

    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                quest.isClaimed -> Color.Gray.copy(alpha = 0.1f)
                                isComplete -> SuccessGreen.copy(alpha = 0.15f)
                                else -> PrimaryGold.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (quest.isClaimed) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        tint = when {
                            quest.isClaimed -> Color.Gray
                            isComplete -> SuccessGreen
                            else -> PrimaryGold
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        quest.title,
                        color = if (quest.isClaimed) TextSecondary else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${quest.current} / ${quest.target}",
                        color = if (quest.isClaimed) TextDim else PrimaryGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                }

                if (quest.isClaimed) {
                    Text("CLAIMED", color = TextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else if (isComplete) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        Text("CLAIM", fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                isComplete -> SuccessGreen
                                else -> PrimaryGold
                            }
                        )
                )
            }
        }
    }
}
