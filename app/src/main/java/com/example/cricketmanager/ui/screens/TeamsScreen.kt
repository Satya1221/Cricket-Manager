package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TeamsScreen(
    viewModel: CricketViewModel,
    onTeamSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.allTeams.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "FRANCHISE SQUADS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Premier League Member Clubs & Rosters",
                    color = CricketGoldLight,
                    fontSize = 12.sp
                )
            }
        }

        items(teams) { team ->
            PremiumCard(
                borderColor = Color(0x33FFD700),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onTeamSelected(team.id) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamBadge(
                        shortCode = team.shortCode,
                        primaryColorHex = team.primaryColorHex,
                        size = 50.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = team.name,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${team.city} • Home: ${team.homeGround}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Record: ${team.matchesWon}W - ${team.matchesLost}L",
                            color = CricketGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}
