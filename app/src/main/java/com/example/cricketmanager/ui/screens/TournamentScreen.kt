package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TournamentScreen(
    viewModel: CricketViewModel,
    onNavigateToMatch: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val standings by viewModel.standings.collectAsState()
    val teams by viewModel.allTeams.collectAsState()

    val sortedStandings = remember(standings) {
        standings.sortedWith(
            compareByDescending<com.example.cricketmanager.data.model.TournamentStandingsEntity> { it.points }
                .thenByDescending { it.netRunRate }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LEAGUE STANDINGS",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Top 4 qualify for playoff eliminators",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketNavySurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "SEASON 1", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Table Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavySurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "#", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "TEAM", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "P", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "W", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "L", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "NRR", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp))
                Text(text = "PTS", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
            }
        }

        itemsIndexed(sortedStandings) { index, item ->
            val position = index + 1
            val team = teams.find { it.id == item.teamId }
            val isPlayoffSpot = position <= 4

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isPlayoffSpot) Color(0xFF141D3E) else CricketNavyCard)
                    .border(
                        BorderStroke(
                            if (isPlayoffSpot) 1.dp else 0.5.dp,
                            if (isPlayoffSpot) CricketGoldBorder else Color(0x22FFD700)
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Position
                Text(
                    text = "$position",
                    color = if (isPlayoffSpot) CricketGold else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp)
                )

                // Team badge & name
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamBadge(
                        shortCode = team?.shortCode ?: "TM",
                        primaryColorHex = team?.primaryColorHex ?: "#1E88E5",
                        size = 30.dp,
                        showBorder = false
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = team?.name ?: "Team $position",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Text(text = "${item.played}", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                Text(text = "${item.won}", color = CricketStadiumGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "${item.lost}", color = CricketRedAccent, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                Text(
                    text = String.format("%+.2f", item.netRunRate),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.width(44.dp)
                )
                Text(
                    text = "${item.points}",
                    color = CricketGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(28.dp)
                )
            }
        }
    }
}
