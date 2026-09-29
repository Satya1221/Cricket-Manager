package com.example.cricketmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PremiumCricketColorScheme = darkColorScheme(
    primary = CricketGold,
    onPrimary = Color(0xFF1B1400),
    primaryContainer = CricketNavySurfaceElevated,
    onPrimaryContainer = CricketGoldLight,
    secondary = CricketStadiumBlue,
    onSecondary = Color.White,
    secondaryContainer = CricketNavyCard,
    onSecondaryContainer = Color(0xFFBCE0FD),
    tertiary = CricketStadiumPurple,
    onTertiary = Color.White,
    background = CricketNavyBackground,
    onBackground = TextPrimary,
    surface = CricketNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = CricketNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = CricketGoldBorder,
    outlineVariant = Color(0xFF28345E)
)

@Composable
fun CricketManagerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PremiumCricketColorScheme,
        typography = Typography,
        content = content
    )
}
