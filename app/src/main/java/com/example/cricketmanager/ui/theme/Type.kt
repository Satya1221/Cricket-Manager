package com.example.cricketmanager.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Compact geometric sports typography. System Sans keeps the app lightweight.
val Typography = Typography(
    displayLarge = TextStyle(FontFamily.Default, FontWeight.Black, 40.sp, 44.sp, letterSpacing = (-1.2).sp),
    displayMedium = TextStyle(FontFamily.Default, FontWeight.ExtraBold, 32.sp, 36.sp, letterSpacing = (-0.8).sp),
    headlineLarge = TextStyle(FontFamily.Default, FontWeight.ExtraBold, 28.sp, 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(FontFamily.Default, FontWeight.Bold, 23.sp, 28.sp, letterSpacing = (-0.2).sp),
    headlineSmall = TextStyle(FontFamily.Default, FontWeight.Bold, 20.sp, 24.sp),
    titleLarge = TextStyle(FontFamily.Default, FontWeight.Bold, 18.sp, 23.sp),
    titleMedium = TextStyle(FontFamily.Default, FontWeight.SemiBold, 15.sp, 20.sp),
    titleSmall = TextStyle(FontFamily.Default, FontWeight.SemiBold, 13.sp, 17.sp),
    bodyLarge = TextStyle(FontFamily.Default, FontWeight.Normal, 15.sp, 21.sp),
    bodyMedium = TextStyle(FontFamily.Default, FontWeight.Normal, 13.sp, 18.sp),
    bodySmall = TextStyle(FontFamily.Default, FontWeight.Normal, 11.sp, 15.sp),
    labelLarge = TextStyle(FontFamily.Default, FontWeight.Bold, 13.sp, 17.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(FontFamily.Default, FontWeight.SemiBold, 11.sp, 14.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(FontFamily.Default, FontWeight.Bold, 9.sp, 12.sp, letterSpacing = 0.8.sp)
)
