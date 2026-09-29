package com.example.cricketmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.ui.graphics.vector.ImageVector

enum class CricketNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    LIVE_MATCH("live_match", "Match", Icons.Default.SportsCricket),
    TEAMS("teams", "Teams", Icons.Default.Group),
    TOURNAMENTS("tournaments", "League", Icons.Default.EmojiEvents),
    STATS("stats", "Stats", Icons.Default.Leaderboard)
}
