package com.senintrerus.circlelock.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senintrerus.circlelock.ui.theme.*

@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceDark,
    contentColor: Color = Color.White,
    gradient: Brush? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "btnScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (gradient != null) Color.Transparent else containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp),
        contentPadding = PaddingValues()
    ) {
        if (gradient != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun LevelCard(level: Int, isUnlocked: Boolean, isCompleted: Boolean, isCurrent: Boolean, onClick: () -> Unit) {
    val borderColor = when {
        isCompleted -> SuccessGreen.copy(alpha = 0.6f)
        isCurrent -> PrimaryGold
        isUnlocked -> PrimaryGold.copy(alpha = 0.2f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    val bgColor = when {
        isCompleted -> SuccessGreen.copy(alpha = 0.12f)
        isCurrent -> PrimaryGold.copy(alpha = 0.1f)
        isUnlocked -> SurfaceDark
        else -> Color.Black.copy(alpha = 0.4f)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && isUnlocked) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .then(
                if (isUnlocked) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                else Modifier
            )
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = SuccessGreen,
                modifier = Modifier.size(22.dp)
            )
        } else if (isUnlocked) {
            Text(
                text = level.toString(),
                color = if (isCurrent) PrimaryGold else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.size(18.dp)
            )
        }

        if (isCurrent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, PrimaryGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, titleColor.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Text(
                title,
                color = titleColor,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("MENU", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                }
                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (titleColor == ErrorRed) ErrorRed else SuccessGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(actionText, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}
