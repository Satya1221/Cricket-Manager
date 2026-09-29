package com.example.cricketmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class CricketNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    SQUAD("squad", "Squad", Icons.Default.Group),
    MATCHES("matches", "Matches", Icons.Default.SportsCricket),
    YOUTH("youth", "Youth", Icons.Default.School),
    MORE("more", "More", Icons.Default.Widgets)
}
