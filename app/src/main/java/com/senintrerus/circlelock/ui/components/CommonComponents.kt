package com.senintrerus.circlelock.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.ui.theme.*

@Composable
fun MenuButton(
    text: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceDark,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
    }
}

@Composable
fun LevelCard(level: Int, isUnlocked: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isUnlocked) SurfaceDark else Color.Black.copy(alpha = 0.3f))
            .then(if (isUnlocked) Modifier.clickable(onClick = onClick) else Modifier)
            .border(
                width = 1.dp, 
                color = if (isUnlocked) PrimaryGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isUnlocked) {
            Text(
                text = level.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        } else {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Locked",
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun GameStatusDialog(
    title: String, 
    titleColor: Color, 
    onBack: () -> Unit, 
    onAction: () -> Unit, 
    actionText: String = "NEXT LEVEL"
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp).clip(RoundedCornerShape(24.dp)).background(SurfaceDark).padding(32.dp)
        ) {
            Text(title, color = titleColor, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text("MENU", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAction,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (titleColor == ErrorRed) ErrorRed else SuccessGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(actionText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
