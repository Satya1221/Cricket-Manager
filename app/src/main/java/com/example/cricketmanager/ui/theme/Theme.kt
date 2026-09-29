package com.example.cricketmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumCricketColorScheme = darkColorScheme(
    primary = CricketGold,
    onPrimary = Color(0xFF071000),
    primaryContainer = CricketNavySurfaceElevated,
    onPrimaryContainer = CricketGoldLight,
    secondary = CricketStadiumBlue,
    onSecondary = Color(0xFF001015),
    secondaryContainer = CricketNavyCard,
    onSecondaryContainer = CricketStadiumNeon,
    tertiary = CricketStadiumPurple,
    onTertiary = Color.White,
    background = CricketNavyBackground,
    onBackground = TextPrimary,
    surface = CricketNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = CricketNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = CricketBorderStrong,
    outlineVariant = CricketBorder
)

@Composable
fun CricketManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PremiumCricketColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
