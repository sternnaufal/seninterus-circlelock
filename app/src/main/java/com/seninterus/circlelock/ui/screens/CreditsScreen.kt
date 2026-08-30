package com.seninterus.circlelock.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CREDITS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(heartScale)
                        .border(2.dp, PrimaryGold.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = PrimaryGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "DEVELOPED BY",
                    color = TextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "SENIN TERUS",
                    color = PrimaryGold,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
                Text(
                    "STUDIO",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .background(PrimaryGold.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(40.dp))

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = ErrorRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "THANKS FOR PLAYING!",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
