package com.senintrerus.circlelock.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.ui.components.MenuButton
import com.senintrerus.circlelock.ui.theme.*
import com.senintrerus.circlelock.util.PlayerStats

@Composable
fun MainScreen(
    onPlayClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onCreditsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val context = LocalContext.current
    val totalCleared = PlayerStats.getTotalClearedCount(context)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PrimaryGold.copy(alpha = 0.05f), Color.Transparent),
                    center = center,
                    radius = size.minDimension / 1.5f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Text(
                    text = "CIRCLE LOCK",
                    color = PrimaryGold,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    modifier = Modifier.animateContentSize()
                )
                Text(
                    text = "SENIN TERUS STUDIO",
                    color = PrimaryGold.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 8.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = "LOCKS OPENED: $totalCleared",
                        color = PrimaryGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton("PLAY", onClick = onPlayClick, containerColor = SuccessGreen)
                MenuButton("OPTIONS", onClick = onOptionsClick)
                MenuButton("CREDITS", onClick = onCreditsClick)
                MenuButton("EXIT", onClick = onExitClick)
            }
        }
    }
}
