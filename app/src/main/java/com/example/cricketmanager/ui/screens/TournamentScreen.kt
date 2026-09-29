package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.TournamentStandingsEntity
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TournamentScreen(
    viewModel: CricketViewModel,
    onNavigateToMatch: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val standings by viewModel.standings.collectAsState()
    val teams by viewModel.allTeams.collectAsState()
    val matches by viewModel.allMatches.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Standings, 1 = Fixtures

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CricketGreenDark)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Tournament",
                        tint = CricketGold,
                        modifier = Modifier.width(48.dp).height(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "PREMIER CRICKET LEAGUE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Season 2026 • 8 Franchises • 20 Overs Format",
                            style = MaterialTheme.typography.bodySmall,
                            color = CricketGold
                        )
                    }
                }
            }
        }

        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CricketGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Points Table", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Fixtures & Results", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Standings Tab
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(24.dp))
                            Text("Franchise", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text("P", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                            Text("W", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                            Text("L", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                            Text("Pts", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(36.dp))
                            Text("NRR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(54.dp))
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(alpha = 0.5f)))

                        standings.forEachIndexed { index, item ->
                            val team = teams.find { it.id == item.teamId }
                            val isTop4 = index < 4

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTop4) CricketGreenPrimary else Color.DarkGray,
                                    modifier = Modifier.width(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = team?.name ?: "Team #${item.teamId}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (isTop4) {
                                        Text(
                                            text = "Playoff Zone",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CricketGreenPrimary
                                        )
                                    }
                                }
                                Text("${item.played}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                                Text("${item.won}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                                Text("${item.lost}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.width(28.dp))
                                Text(
                                    text = "${item.points}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGreenPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(36.dp)
                                )
                                val sign = if (item.netRunRate >= 0) "+" else ""
                                Text(
                                    text = "$sign${"%.2f".format(item.netRunRate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(54.dp)
                                )
                            }
                            if (index < standings.size - 1) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(alpha = 0.2f)))
                            }
                        }
                    }
                }
            }
        } else {
            // Fixtures Tab
            items(matches) { match ->
                FixtureMatchCard(
                    match = match,
                    teams = teams,
                    onPlayOrView = { onNavigateToMatch(match.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FixtureMatchCard(
    match: MatchEntity,
    teams: List<TeamEntity>,
    onPlayOrView: () -> Unit
) {
    val team1 = teams.find { it.id == match.team1Id }
    val team2 = teams.find { it.id == match.team2Id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (match.status) {
                        MatchStatus.IN_PROGRESS -> Color(0xFFC62828)
                        MatchStatus.COMPLETED -> CricketGreenPrimary
                        MatchStatus.UPCOMING -> Color.Gray
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = match.status.name.replace("_", " "),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${match.oversPerInnings} Overs Match",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${team1?.name ?: "Team 1"} vs ${team2?.name ?: "Team 2"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (match.status == MatchStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${team1?.shortCode}: ${match.inn1Runs}/${match.inn1Wickets} • ${team2?.shortCode}: ${match.inn2Runs}/${match.inn2Wickets}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = match.resultDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = CricketGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            } else if (match.status == MatchStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Current: ${match.currentRuns}/${match.currentWickets} (${match.currentOversFormatted} ov)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onPlayOrView,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
            ) {
                Text(if (match.status == MatchStatus.COMPLETED) "Scorecard" else "Play Match")
            }
        }
    }
}
