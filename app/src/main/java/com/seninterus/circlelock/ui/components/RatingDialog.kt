package com.seninterus.circlelock.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seninterus.circlelock.ui.theme.*

@Composable
fun RatingDialog(
    onRate: () -> Unit,
    onDismiss: () -> Unit,
    onLater: () -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        titleContentColor = PrimaryGold,
        textContentColor = Color.White,
        title = {
            Text(
                "ENJOYING THE GAME?",
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                letterSpacing = 1.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Your rating helps us improve!",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "$star stars",
                            tint = if (star <= selectedStars) PrimaryGold else Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedStars = star }
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    when (selectedStars) {
                        5 -> "Awesome!"
                        4 -> "Great!"
                        3 -> "Good!"
                        else -> "We'll do better!"
                    },
                    color = when (selectedStars) {
                        5 -> SuccessGreen
                        4 -> PrimaryGold
                        3 -> AccentBlue
                        else -> ErrorRed
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "RATE ON PLAY STORE",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onLater,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("LATER", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
            }
        }
    )
}
