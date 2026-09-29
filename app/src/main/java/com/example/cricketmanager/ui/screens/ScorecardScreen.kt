package com.example.cricketmanager.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.ExtraType
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.viewmodel.CricketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    viewModel: CricketViewModel,
    matchId: Long,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val currentMatch by viewModel.currentMatch.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()
    val matchBalls by viewModel.matchBalls.collectAsState()
    val battingPlayers by viewModel.battingTeamPlayers.collectAsState()
    val bowlingPlayers by viewModel.bowlingTeamPlayers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    val match = currentMatch
    if (match == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading scorecard...")
        }
        return
    }

    val team1 = allTeams.find { it.id == match.team1Id }
    val team2 = allTeams.find { it.id == match.team2Id }

    val inn1BattingTeam = if (match.battingFirstTeamId == match.team1Id) team1 else team2
    val inn2BattingTeam = if (match.battingFirstTeamId == match.team1Id) team2 else team1

    val inn1Balls = matchBalls.filter { it.inningsNumber == 1 }
    val inn2Balls = matchBalls.filter { it.inningsNumber == 2 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Scorecard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("scorecard_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CricketGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Match Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${team1?.name} vs ${team2?.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (match.resultDescription.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = match.resultDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = CricketGreenPrimary
                        )
                    }
                }
            }

            // Innings Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CricketGreenPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "${inn1BattingTeam?.shortCode ?: "Inn 1"} (${match.inn1Runs}/${match.inn1Wickets})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "${inn2BattingTeam?.shortCode ?: "Inn 2"} (${match.inn2Runs}/${match.inn2Wickets})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            val currentInningsNum = selectedTab + 1
            val currentBalls = if (currentInningsNum == 1) inn1Balls else inn2Balls
            val battingTeam = if (currentInningsNum == 1) inn1BattingTeam else inn2BattingTeam
            val totalRuns = if (currentInningsNum == 1) match.inn1Runs else match.inn2Runs
            val totalWickets = if (currentInningsNum == 1) match.inn1Wickets else match.inn2Wickets
            val totalBalls = if (currentInningsNum == 1) match.inn1Balls else match.inn2Balls

            InningsDetailView(
                inningsNum = currentInningsNum,
                teamName = battingTeam?.name ?: "Team",
                totalRuns = totalRuns,
                totalWickets = totalWickets,
                totalBalls = totalBalls,
                balls = currentBalls,
                allPlayers = battingPlayers + bowlingPlayers
            )
        }
    }
}

@Composable
fun InningsDetailView(
    inningsNum: Int,
    teamName: String,
    totalRuns: Int,
    totalWickets: Int,
    totalBalls: Int,
    balls: List<BallEventEntity>,
    allPlayers: List<PlayerEntity>
) {
    val overs = totalBalls / 6
    val bInOver = totalBalls % 6
    val crr = if (totalBalls > 0) (totalRuns.toDouble() / (totalBalls / 6.0)) else 0.0

    // Unique batsmen who faced balls
    val strikerIds = balls.map { it.strikerId }.distinct()
    val bowlerIds = balls.map { it.bowlerId }.distinct()

    val extrasTotal = balls.sumOf { it.extraRuns }
    val wides = balls.count { it.extraType == ExtraType.WIDE }
    val noBalls = balls.count { it.extraType == ExtraType.NO_BALL }
    val byes = balls.filter { it.extraType == ExtraType.BYE }.sumOf { it.extraRuns }
    val legByes = balls.filter { it.extraType == ExtraType.LEG_BYE }.sumOf { it.extraRuns }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Innings Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "$teamName Innings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Overs: $overs.$bInOver • Run Rate: ${"%.2f".format(crr)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Text(
                    text = "$totalRuns/$totalWickets",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = CricketGreenPrimary
                )
            }
        }

        // Batting Scorecard Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Batter", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.8f))
                        Text("R", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                        Text("B", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                        Text("4s", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                        Text("6s", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                        Text("SR", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(alpha = 0.5f)))

                    if (strikerIds.isEmpty()) {
                        Text(
                            text = "No batting data recorded yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    strikerIds.forEach { pId ->
                        val player = allPlayers.find { it.id == pId }
                        val playerBalls = balls.filter { it.strikerId == pId }
                        val r = playerBalls.sumOf { it.runsBat }
                        val b = playerBalls.filter { it.extraType != ExtraType.WIDE }.size
                        val fours = playerBalls.count { it.runsBat == 4 }
                        val sixes = playerBalls.count { it.runsBat == 6 }
                        val sr = if (b > 0) (r.toDouble() / b) * 100 else 0.0
                        val dismissalBall = balls.find { it.dismissedPlayerId == pId && it.isWicket }
                        val dismissalText = if (dismissalBall != null) "${dismissalBall.wicketType}" else "not out"

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.8f)) {
                                    Text(
                                        text = player?.name ?: "Batter #$pId",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = dismissalText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (dismissalBall != null) Color.Gray else CricketGreenPrimary
                                    )
                                }
                                Text("$r", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                                Text("$b", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                                Text("$fours", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                                Text("$sixes", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
                                Text("${"%.1f".format(sr)}", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                            }
                        }
                    }
                }
            }
        }

        // Extras Row
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Extras: $extrasTotal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(wd $wides, nb $noBalls, b $byes, lb $legByes)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // Bowling Scorecard Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "BOWLING",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bowler", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.0f))
                        Text("O", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                        Text("R", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                        Text("W", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                        Text("Econ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(alpha = 0.5f)))

                    bowlerIds.forEach { bId ->
                        val bowler = allPlayers.find { it.id == bId }
                        val bBalls = balls.filter { it.bowlerId == bId }
                        val legBalls = bBalls.count { it.isLegalBall }
                        val runs = bBalls.sumOf { it.totalRuns }
                        val wkts = bBalls.count { it.isWicket && it.wicketType != com.example.cricketmanager.data.model.WicketType.RUN_OUT }
                        val bOvers = legBalls / 6
                        val bBallsRem = legBalls % 6
                        val econ = if (legBalls > 0) (runs.toDouble() / (legBalls / 6.0)) else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(bowler?.name ?: "Bowler #$bId", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2.0f))
                            Text("$bOvers.$bBallsRem", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                            Text("$runs", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                            Text("$wkts", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CricketGreenPrimary, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
                            Text("${"%.1f".format(econ)}", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                        }
                    }
                }
            }
        }
    }
}
