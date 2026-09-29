package com.example.cricketmanager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CricketGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = CricketGreenContainer,
    onPrimaryContainer = CricketGreenOnContainer,
    secondary = CricketGoldDark,
    onSecondary = Color.White,
    secondaryContainer = CricketGoldLight,
    onSecondaryContainer = Color(0xFF261900),
    tertiary = CricketOrange,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF191C1A),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C1A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3F4943),
    outline = OutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7BD7B2),
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005138),
    onPrimaryContainer = CricketGreenContainer,
    secondary = CricketGoldLight,
    onSecondary = Color(0xFF3E2E00),
    secondaryContainer = Color(0xFF5A4400),
    onSecondaryContainer = CricketGoldLight,
    tertiary = Color(0xFFFFB59C),
    onTertiary = Color(0xFF5B1B02),
    background = Color(0xFF101512),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF181D1A),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF252D28),
    onSurfaceVariant = Color(0xFFC0C9C2),
    outline = Color(0xFF8A938C)
)

@Composable
fun CricketManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
