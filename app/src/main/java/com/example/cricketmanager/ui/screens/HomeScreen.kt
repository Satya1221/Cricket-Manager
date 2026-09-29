package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.R
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.TossDecision
import com.example.cricketmanager.data.model.TournamentStandingsEntity
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun HomeScreen(
    viewModel: CricketViewModel,
    onNavigateToMatch: (Long) -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.allTeams.collectAsState()
    val activeMatch by viewModel.activeMatch.collectAsState()
    val standings by viewModel.standings.collectAsState()
    val topScorers by viewModel.topRunScorers.collectAsState()
    val topBowlers by viewModel.topWicketTakers.collectAsState()

    var showNewMatchDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HeroBanner(
                onStartQuickMatch = { showNewMatchDialog = true }
            )
        }

        // Active Match Card
        if (activeMatch != null && activeMatch?.status == MatchStatus.IN_PROGRESS) {
            item {
                ActiveMatchCard(
                    match = activeMatch!!,
                    teams = teams,
                    onResumeMatch = { onNavigateToMatch(activeMatch!!.id) }
                )
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = "Manager Dashboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "New Match",
                    subtitle = "T20 / Custom",
                    icon = Icons.Default.SportsCricket,
                    containerColor = CricketGreenPrimary,
                    contentColor = Color.White,
                    onClick = { showNewMatchDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_new_match_btn")
                )
                QuickNavCard(
                    title = "Squads",
                    subtitle = "${teams.size} Teams",
                    icon = Icons.Default.Group,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = onNavigateToTeams,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_teams_btn")
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "League",
                    subtitle = "Points Table",
                    icon = Icons.Default.EmojiEvents,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = onNavigateToTournaments,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_league_btn")
                )
                QuickNavCard(
                    title = "Statistics",
                    subtitle = "Orange / Purple",
                    icon = Icons.Default.Leaderboard,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onNavigateToStats,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_stats_btn")
                )
            }
        }

        // Top Performers Widget
        item {
            LeadersWidget(
                topBatter = topScorers.firstOrNull(),
                topBowler = topBowlers.firstOrNull(),
                onViewAll = onNavigateToStats
            )
        }

        // Standings Preview Widget
        item {
            LeagueTablePreview(
                standings = standings.take(4),
                teams = teams,
                onViewFullTable = onNavigateToTournaments
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showNewMatchDialog && teams.size >= 2) {
        NewMatchDialog(
            teams = teams,
            onDismiss = { showNewMatchDialog = false },
            onConfirm = { team1Id, team2Id, overs, tossWinner, tossDecision ->
                showNewMatchDialog = false
                viewModel.startNewMatch(
                    team1Id = team1Id,
                    team2Id = team2Id,
                    overs = overs,
                    tossWinnerId = tossWinner,
                    tossDecision = tossDecision
                )
                // Navigation to live match happens automatically via active match or tab
            }
        )
    }
}

@Composable
fun HeroBanner(
    onStartQuickMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.cricket_stadium_banner_1790682263928),
                contentDescription = "Cricket Stadium Floodlights",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "CRICKET MANAGER 2026",
                    style = MaterialTheme.typography.labelLarge,
                    color = CricketGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Lead Your Franchise to Glory",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onStartQuickMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("hero_quick_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Play Match",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveMatchCard(
    match: MatchEntity,
    teams: List<TeamEntity>,
    onResumeMatch: () -> Unit
) {
    val team1 = teams.find { it.id == match.team1Id }
    val team2 = teams.find { it.id == match.team2Id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onResumeMatch)
            .testTag("active_match_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFC62828),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LIVE NOW",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Innings ${match.currentInnings} • ${match.oversPerInnings} Overs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${team1?.name ?: "Team 1"} vs ${team2?.name ?: "Team 2"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Score: ${match.currentRuns}/${match.currentWickets} (${match.currentOversFormatted} ov)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenPrimary
                    )
                    if (match.currentInnings == 2 && match.targetRuns > 0) {
                        Text(
                            text = "Target: ${match.targetRuns} (Need ${maxOf(0, match.targetRuns - match.currentRuns)} runs)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Button(
                    onClick = onResumeMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    modifier = Modifier.testTag("resume_match_btn")
                ) {
                    Text("Resume")
                }
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(95.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun LeadersWidget(
    topBatter: PlayerEntity?,
    topBowler: PlayerEntity?,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tournament Leaders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAll) {
                    Text("Full Stats")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Orange Cap
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ORANGE CAP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = topBatter?.name ?: "Rohit Ray",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${topBatter?.runsScored ?: 135} Runs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBF360C)
                        )
                    }
                }
                // Purple Cap
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "PURPLE CAP",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )
                        Text(
                            text = topBowler?.name ?: "Jasprit Singh",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${topBowler?.wicketsTaken ?: 6} Wickets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A148C)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueTablePreview(
    standings: List<TournamentStandingsEntity>,
    teams: List<TeamEntity>,
    onViewFullTable: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Premier League Standings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewFullTable) {
                    Text("View All")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("#", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.width(24.dp))
                Text("Team", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.weight(1f))
                Text("P", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.width(28.dp))
                Text("W", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.width(28.dp))
                Text("Pts", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                Text("NRR", style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.width(52.dp))
            }
            standings.forEachIndexed { index, item ->
                val team = teams.find { it.id == item.teamId }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = team?.name ?: "Team",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "${item.played}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(28.dp))
                    Text(text = "${item.won}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(28.dp))
                    Text(
                        text = "${item.points}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CricketGreenPrimary,
                        modifier = Modifier.width(36.dp)
                    )
                    val sign = if (item.netRunRate >= 0) "+" else ""
                    Text(
                        text = "$sign${"%.2f".format(item.netRunRate)}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(52.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMatchDialog(
    teams: List<TeamEntity>,
    onDismiss: () -> Unit,
    onConfirm: (team1Id: Long, team2Id: Long, overs: Int, tossWinner: Long, tossDecision: TossDecision) -> Unit
) {
    var team1Index by remember { mutableIntStateOf(0) }
    var team2Index by remember { mutableIntStateOf(1.coerceAtMost(teams.size - 1)) }
    var selectedOvers by remember { mutableIntStateOf(20) }
    var tossWinnerIndex by remember { mutableIntStateOf(0) } // 0 = team1, 1 = team2
    var tossDecision by remember { mutableStateOf(TossDecision.BAT) }

    val team1 = teams.getOrNull(team1Index) ?: teams[0]
    val team2 = teams.getOrNull(team2Index) ?: teams[1]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Start New Match", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Team 1 Selection
                Text("Team 1 (Home):", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    teams.take(4).forEachIndexed { idx, t ->
                        FilterChip(
                            selected = team1Index == idx,
                            onClick = { team1Index = idx },
                            label = { Text(t.shortCode) }
                        )
                    }
                }

                // Team 2 Selection
                Text("Team 2 (Away):", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    teams.drop(1).take(4).forEachIndexed { idx, t ->
                        val actualIdx = idx + 1
                        FilterChip(
                            selected = team2Index == actualIdx,
                            onClick = { team2Index = actualIdx },
                            label = { Text(t.shortCode) }
                        )
                    }
                }

                // Overs Selection
                Text("Match Overs:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 20).forEach { ov ->
                        FilterChip(
                            selected = selectedOvers == ov,
                            onClick = { selectedOvers = ov },
                            label = { Text("$ov Overs") }
                        )
                    }
                }

                // Toss
                Text("Toss Winner & Decision:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tossWinnerIndex == 0,
                        onClick = { tossWinnerIndex = 0 },
                        label = { Text(team1.shortCode) }
                    )
                    FilterChip(
                        selected = tossWinnerIndex == 1,
                        onClick = { tossWinnerIndex = 1 },
                        label = { Text(team2.shortCode) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tossDecision == TossDecision.BAT,
                        onClick = { tossDecision = TossDecision.BAT },
                        label = { Text("Elected to Bat") }
                    )
                    FilterChip(
                        selected = tossDecision == TossDecision.BOWL,
                        onClick = { tossDecision = TossDecision.BOWL },
                        label = { Text("Elected to Bowl") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val winnerId = if (tossWinnerIndex == 0) team1.id else team2.id
                    onConfirm(team1.id, team2.id, selectedOvers, winnerId, tossDecision)
                },
                modifier = Modifier.testTag("confirm_new_match_btn")
            ) {
                Text("Start Match")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
