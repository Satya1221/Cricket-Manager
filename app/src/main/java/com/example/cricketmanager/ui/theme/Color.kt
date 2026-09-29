package com.example.cricketmanager.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Cricket Theme - Deep Navy, Gold, Stadium Glow
val CricketDarkNavy = Color(0xFF090D1C)
val CricketNavyBackground = Color(0xFF0D1226)
val CricketNavySurface = Color(0xFF131A36)
val CricketNavySurfaceElevated = Color(0xFF1C2448)
val CricketNavyCard = Color(0xFF172040)
val CricketNavyGlass = Color(0xCC131A38)

// Gold Accents
val CricketGold = Color(0xFFFFD700)
val CricketGoldLight = Color(0xFFFFE082)
val CricketGoldDark = Color(0xFFC79A00)
val CricketGoldAccent = Color(0xFFFFA000)
val CricketGoldBorder = Color(0x66FFD700)
val CricketGoldGlow = Color(0x33FFD700)

// Stadium Lighting & Accents
val CricketStadiumBlue = Color(0xFF1E88E5)
val CricketStadiumPurple = Color(0xFF7E57C2)
val CricketStadiumNeon = Color(0xFF00E5FF)
val CricketStadiumGreen = Color(0xFF00E676)
val CricketRedAccent = Color(0xFFFF5252)

// Turf & Pitch
val TurfGreenDark = Color(0xFF144D29)
val TurfGreen = Color(0xFF1B5E20)
val TurfGreenLight = Color(0xFF2E7D32)
val PitchClay = Color(0xFFC4A470)
val PitchLineWhite = Color(0xFFEEEEEE)

// Typography & Content
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B9D0)
val TextMuted = Color(0xFF78849E)
val TextGold = Color(0xFFFFDF79)

// Gradients
val GoldGradient = Brush.horizontalGradient(
    colors = listOf(CricketGoldDark, CricketGold, CricketGoldLight)
)

val CardGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1C2448), Color(0xFF11172E))
)

val StadiumGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0A0F24), Color(0xFF080C1E))
)

val PitchGrassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E5B2C), Color(0xFF143D1D))
)
