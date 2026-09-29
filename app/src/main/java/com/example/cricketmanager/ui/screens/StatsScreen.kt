package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun StatsScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val topScorers by viewModel.topRunScorers.collectAsState()
    val topBowlers by viewModel.topWicketTakers.collectAsState()
    val teams by viewModel.allTeams.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Orange Cap (Batting)", "Purple Cap (Bowling)")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "TOURNAMENT HONOURS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Individual leaderboards & award races",
                    color = CricketGoldLight,
                    fontSize = 12.sp
                )
            }
        }

        item {
            TabSelector(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        if (selectedTab == 0) {
            // Orange Cap Leader
            val leader = topScorers.firstOrNull()
            if (leader != null) {
                item {
                    val leaderTeam = teams.find { it.id == leader.teamId }
                    PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4A3200)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGold, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "CURRENT ORANGE CAP HOLDER", color = CricketGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = leader.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${leaderTeam?.name ?: ""} • ${leader.runsScored} Runs (Avg: ${String.format("%.1f", leader.battingAverage)})", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Top Run Scorers")
            }

            itemsIndexed(topScorers) { index, player ->
                val team = teams.find { it.id == player.teamId }
                StatLeaderRow(
                    rank = index + 1,
                    player = player,
                    teamName = team?.name ?: "",
                    statText = "${player.runsScored} Runs",
                    subText = "Avg: ${String.format("%.1f", player.battingAverage)} • HS: ${player.highestScore}"
                )
            }
        } else {
            // Purple Cap Leader
            val leader = topBowlers.firstOrNull()
            if (leader != null) {
                item {
                    val leaderTeam = teams.find { it.id == leader.teamId }
                    PremiumCard(borderColor = CricketStadiumPurple, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF27134A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketStadiumPurple, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "CURRENT PURPLE CAP HOLDER", color = CricketStadiumPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = leader.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${leaderTeam?.name ?: ""} • ${leader.wicketsTaken} Wickets", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Top Wicket Takers")
            }

            itemsIndexed(topBowlers) { index, player ->
                val team = teams.find { it.id == player.teamId }
                StatLeaderRow(
                    rank = index + 1,
                    player = player,
                    teamName = team?.name ?: "",
                    statText = "${player.wicketsTaken} Wickets",
                    subText = "Econ: ${String.format("%.1f", player.bowlingEconomy)} • Overs: ${player.oversFormatted}"
                )
            }
        }
    }
}

@Composable
fun StatLeaderRow(
    rank: Int,
    player: PlayerEntity,
    teamName: String,
    statText: String,
    subText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketNavySurfaceElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (rank == 1) CricketGold else CricketNavyCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                color = if (rank == 1) Color.Black else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = player.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "$teamName • $subText", color = TextSecondary, fontSize = 11.sp)
        }

        Text(text = statText, color = CricketGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
