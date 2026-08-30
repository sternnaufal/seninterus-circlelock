package com.seninterus.circlelock.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val PrimaryGold = Color(0xFFFFD700)
val SecondaryGold = Color(0xFFC5A021)
val BackgroundDark = Color(0xFF0A0A0A)
val SurfaceDark = Color(0xFF161616)
val SurfaceLight = Color(0xFF1E1E1E)
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFCF6679)
val AccentBlue = Color(0xFF4FC3F7)
val AccentPurple = Color(0xFFCE93D8)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9E9E9E)
val TextDim = Color(0xFF616161)

val GoldGradient = Brush.verticalGradient(
    colors = listOf(PrimaryGold, SecondaryGold)
)

val GreenGradient = Brush.verticalGradient(
    colors = listOf(SuccessGreen, Color(0xFF388E3C))
)

val RedGradient = Brush.verticalGradient(
    colors = listOf(ErrorRed, Color(0xFFB71C1C))
)

val SurfaceGradient = Brush.verticalGradient(
    colors = listOf(SurfaceLight, SurfaceDark)
)

val GlowGold = Brush.radialGradient(
    colors = listOf(PrimaryGold.copy(alpha = 0.15f), Color.Transparent),
    radius = 300f
)

val GlowGreen = Brush.radialGradient(
    colors = listOf(SuccessGreen.copy(alpha = 0.15f), Color.Transparent),
    radius = 300f
)
