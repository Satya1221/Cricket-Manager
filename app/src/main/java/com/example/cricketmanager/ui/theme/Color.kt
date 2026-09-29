package com.example.cricketmanager.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium sports dark palette
val CricketDarkNavy = Color(0xFF0B101D)
val CricketNavyBackground = Color(0xFF0B101D)
val CricketNavySurface = Color(0xFF121824)
val CricketNavySurfaceElevated = Color(0xFF182131)
val CricketNavyCard = Color(0xFF151D2B)
val CricketNavyGlass = Color(0xD9121824)

// High-energy accents
val CricketGold = Color(0xFFB8FF3D)
val CricketGoldLight = Color(0xFFD6FF8A)
val CricketGoldDark = Color(0xFF82C51E)
val CricketGoldAccent = Color(0xFFFF9F43)
val CricketGoldBorder = Color(0x66B8FF3D)
val CricketGoldGlow = Color(0x33B8FF3D)

val CricketStadiumBlue = Color(0xFF24C8FF)
val CricketStadiumPurple = Color(0xFF8B6CFF)
val CricketStadiumNeon = Color(0xFF63E6FF)
val CricketStadiumGreen = Color(0xFF46E6A2)
val CricketRedAccent = Color(0xFFFF5263)
val CricketOrangeAccent = Color(0xFFFF9F43)

// Neutral glass surfaces
val CricketGlass = Color(0x14FFFFFF)
val CricketGlassStrong = Color(0x1FFFFFFF)
val CricketBorder = Color(0x1AFFFFFF)
val CricketBorderStrong = Color(0x2EFFFFFF)

// Turf & pitch
val TurfGreenDark = Color(0xFF103A27)
val TurfGreen = Color(0xFF185B35)
val TurfGreenLight = Color(0xFF2F8A52)
val PitchClay = Color(0xFFC4A470)
val PitchLineWhite = Color(0xFFEEEEEE)

// Typography
val TextPrimary = Color(0xFFF5F7FA)
val TextSecondary = Color(0xFFB4BECC)
val TextMuted = Color(0xFF748096)
val TextGold = Color(0xFFD6FF8A)
val TextDisabled = Color(0xFF4B5668)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(CricketGoldDark, CricketGold, CricketGoldLight)
)

val CardGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF182131), Color(0xFF101722))
)

val StadiumGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0D1422), Color(0xFF080C15))
)

val PitchGrassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E5B2C), Color(0xFF143D1D))
)
