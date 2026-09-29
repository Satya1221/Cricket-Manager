package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.ExtraType
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.WicketType
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.ui.theme.CricketOrange
import com.example.cricketmanager.ui.theme.CricketRed
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun LiveMatchScreen(
    viewModel: CricketViewModel,
    onNavigateToScorecard: (Long) -> Unit,
    onStartNewMatchRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()
    val matchBalls by viewModel.matchBalls.collectAsState()
    val battingPlayers by viewModel.battingTeamPlayers.collectAsState()
    val bowlingPlayers by viewModel.bowlingTeamPlayers.collectAsState()
    val mindset by viewModel.battingMindset.collectAsState()
    val bowlingPlan by viewModel.bowlingPlan.collectAsState()

    var showWicketDialog by remember { mutableStateOf(false) }
    var showTacticsDialog by remember { mutableStateOf(false) }

    if (currentMatch == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsCricket,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = CricketGreenPrimary
                )
                Text(
                    text = "No Live Match in Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Start a new match or pick an existing fixture to score and simulate ball-by-ball.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Button(
                    onClick = onStartNewMatchRequested,
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    modifier = Modifier.testTag("start_match_from_live_btn")
                ) {
                    Text("Start New Match")
                }
            }
        }
        return
    }

    val match = currentMatch!!
    val battingTeam = allTeams.find { it.id == match.battingTeamId }
    val bowlingTeam = allTeams.find { it.id == match.bowlingTeamId }
    val striker = battingPlayers.find { it.id == match.currentStrikerId }
    val nonStriker = battingPlayers.find { it.id == match.currentNonStrikerId }
    val bowler = bowlingPlayers.find { it.id == match.currentBowlerId }

    // Innings balls
    val currentInningsBalls = matchBalls.filter { it.inningsNumber == match.currentInnings }
    val legalBallsThisInnings = currentInningsBalls.filter { it.isLegalBall }.size
    val currentOverBalls = currentInningsBalls.takeLastWhile {
        it.overNumber == (legalBallsThisInnings / 6) || (legalBallsThisInnings % 6 == 0 && it.overNumber == (legalBallsThisInnings / 6) - 1)
    }.takeLast(8)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            MatchScorecardHeader(
                match = match,
                battingTeam = battingTeam,
                bowlingTeam = bowlingTeam,
                onViewScorecard = { onNavigateToScorecard(match.id) }
            )
        }

        // Match Completed Banner
        if (match.status == MatchStatus.COMPLETED) {
            item {
                MatchCompletedCard(
                    match = match,
                    onViewScorecard = { onNavigateToScorecard(match.id) },
                    onStartNewMatch = onStartNewMatchRequested
                )
            }
        } else {
            // Batsmen & Bowler Cards
            item {
                ActiveParticipantsCard(
                    striker = striker,
                    nonStriker = nonStriker,
                    bowler = bowler,
                    balls = currentInningsBalls
                )
            }

            // Current Over Timeline
            item {
                OverTimelineCard(
                    balls = currentOverBalls,
                    overNumber = (match.currentBalls / 6) + 1
                )
            }

            // Tactics & Coach Controls
            item {
                TacticsBanner(
                    mindset = mindset,
                    plan = bowlingPlan,
                    onOpenTactics = { showTacticsDialog = true }
                )
            }

            // Scoring Controls
            item {
                ScoringControlPanel(
                    onRunsScored = { runs ->
                        viewModel.recordManualBall(
                            runsBat = runs,
                            extraType = ExtraType.NONE,
                            extraRuns = 0,
                            isWicket = false,
                            wicketType = WicketType.NONE
                        )
                    },
                    onExtra = { extra ->
                        viewModel.recordManualBall(
                            runsBat = 0,
                            extraType = extra,
                            extraRuns = 1,
                            isWicket = false,
                            wicketType = WicketType.NONE
                        )
                    },
                    onWicketClick = { showWicketDialog = true },
                    onSimulateBall = { viewModel.simulateNextBall() },
                    onSimulateOver = { viewModel.simulateCurrentOver() },
                    onAutoSimulate = { viewModel.autoSimulateInnings() },
                    onUndo = { viewModel.undoLastBall() }
                )
            }
        }

        // Live Commentary Feed
        item {
            Text(
                text = "Live Commentary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(matchBalls.takeLast(15).reversed()) { ball ->
            CommentaryItem(ball = ball)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showWicketDialog) {
        WicketDialog(
            onDismiss = { showWicketDialog = false },
            onConfirmWicket = { wicketType ->
                showWicketDialog = false
                viewModel.recordManualBall(
                    runsBat = 0,
                    extraType = ExtraType.NONE,
                    extraRuns = 0,
                    isWicket = true,
                    wicketType = wicketType
                )
            }
        )
    }

    if (showTacticsDialog) {
        TacticsDialog(
            currentMindset = mindset,
            currentPlan = bowlingPlan,
            onDismiss = { showTacticsDialog = false },
            onSave = { newMindset, newPlan ->
                showTacticsDialog = false
                viewModel.setBattingMindset(newMindset)
                viewModel.setBowlingPlan(newPlan)
            }
        )
    }
}

@Composable
fun MatchScorecardHeader(
    match: MatchEntity,
    battingTeam: TeamEntity?,
    bowlingTeam: TeamEntity?,
    onViewScorecard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_scorecard_header"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CricketGreenDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${battingTeam?.name ?: "Batting"} vs ${bowlingTeam?.name ?: "Bowling"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(
                    onClick = onViewScorecard,
                    modifier = Modifier.testTag("view_full_scorecard_btn")
                ) {
                    Text("Scorecard", color = CricketGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${match.currentRuns}/${match.currentWickets}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${match.currentOversFormatted} / ${match.oversPerInnings} ov)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "CRR: ${"%.2f".format(match.currentRunRate)}" +
                                if (match.currentInnings == 2 && match.requiredRunRate > 0)
                                    " • RRR: ${"%.2f".format(match.requiredRunRate)}" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CricketGold
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Innings ${match.currentInnings}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Target Info
            if (match.currentInnings == 2 && match.targetRuns > 0 && match.status == MatchStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(10.dp))
                val runsNeeded = maxOf(0, match.targetRuns - match.inn2Runs)
                val ballsLeft = maxOf(0, (match.oversPerInnings * 6) - match.inn2Balls)
                Surface(
                    color = CricketGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Need $runsNeeded runs from $ballsLeft balls to win (Target: ${match.targetRuns})",
                        color = CricketGold,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveParticipantsCard(
    striker: PlayerEntity?,
    nonStriker: PlayerEntity?,
    bowler: PlayerEntity?,
    balls: List<BallEventEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Batters section
            Text(
                text = "BATTERS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CricketGreenPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Striker row
            if (striker != null) {
                val strikerBalls = balls.filter { it.strikerId == striker.id }
                val runs = strikerBalls.sumOf { it.runsBat }
                val faced = strikerBalls.filter { it.extraType != ExtraType.WIDE }.size
                val fours = strikerBalls.count { it.runsBat == 4 }
                val sixes = strikerBalls.count { it.runsBat == 6 }
                val sr = if (faced > 0) (runs.toDouble() / faced) * 100 else 0.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${striker.name} *",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = CricketGreenPrimary
                        )
                    }
                    Text(
                        text = "$runs ($faced)  4s: $fours  6s: $sixes  SR: ${"%.1f".format(sr)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Non-striker row
            if (nonStriker != null) {
                val nonStrikerBalls = balls.filter { it.strikerId == nonStriker.id }
                val runs = nonStrikerBalls.sumOf { it.runsBat }
                val faced = nonStrikerBalls.filter { it.extraType != ExtraType.WIDE }.size
                val fours = nonStrikerBalls.count { it.runsBat == 4 }
                val sixes = nonStrikerBalls.count { it.runsBat == 6 }
                val sr = if (faced > 0) (runs.toDouble() / faced) * 100 else 0.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nonStriker.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "$runs ($faced)  4s: $fours  6s: $sixes  SR: ${"%.1f".format(sr)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.LightGray.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.height(10.dp))

            // Bowler section
            Text(
                text = "BOWLER",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CricketOrange
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (bowler != null) {
                val bowlerBalls = balls.filter { it.bowlerId == bowler.id }
                val runsConceded = bowlerBalls.sumOf { it.totalRuns }
                val wickets = bowlerBalls.count { it.isWicket && it.wicketType != WicketType.RUN_OUT }
                val legalBalls = bowlerBalls.count { it.isLegalBall }
                val overs = legalBalls / 6
                val bInOver = legalBalls % 6
                val econ = if (legalBalls > 0) (runsConceded.toDouble() / (legalBalls / 6.0)) else 0.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bowler.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$overs.$bInOver-$wickets-$runsConceded  Econ: ${"%.1f".format(econ)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = CricketOrange
                    )
                }
            }
        }
    }
}

@Composable
fun OverTimelineCard(
    balls: List<BallEventEntity>,
    overNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Over $overNumber:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(65.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (balls.isEmpty()) {
                    item {
                        Text(
                            text = "New Over",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(balls) { ball ->
                        BallChip(label = ball.shortLabel, isWicket = ball.isWicket, runs = ball.runsBat)
                    }
                }
            }
        }
    }
}

@Composable
fun BallChip(label: String, isWicket: Boolean, runs: Int) {
    val (bgColor, textColor) = when {
        isWicket -> CricketRed to Color.White
        runs == 6 -> CricketGold to Color.Black
        runs == 4 -> CricketGreenPrimary to Color.White
        label.contains("wd") || label.contains("nb") -> CricketOrange to Color.White
        label == "•" -> Color(0xFFE0E0E0) to Color.DarkGray
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TacticsBanner(
    mindset: BattingMindset,
    plan: BowlingPlan,
    onOpenTactics: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenTactics),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Coach Tactics (Tap to adjust)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = "Batting: ${mindset.displayName} • Bowling: ${plan.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CricketGreenPrimary
                )
            }
            Text(
                text = "Change",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = CricketGreenPrimary
            )
        }
    }
}

@Composable
fun ScoringControlPanel(
    onRunsScored: (Int) -> Unit,
    onExtra: (ExtraType) -> Unit,
    onWicketClick: () -> Unit,
    onSimulateBall: () -> Unit,
    onSimulateOver: () -> Unit,
    onAutoSimulate: () -> Unit,
    onUndo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Runs Buttons Row
            Text(
                text = "SCORE BALL",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 1, 2, 3, 4, 6).forEach { runs ->
                    Button(
                        onClick = { onRunsScored(runs) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("score_btn_$runs"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (runs) {
                                4 -> CricketGreenPrimary
                                6 -> CricketGold
                                0 -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            contentColor = when (runs) {
                                4 -> Color.White
                                6 -> Color.Black
                                0 -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (runs == 0) "•" else "$runs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Extras & Wicket Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { onExtra(ExtraType.WIDE) },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Wide", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = { onExtra(ExtraType.NO_BALL) },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("No Ball", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = onWicketClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("wicket_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("WICKET", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onUndo,
                    modifier = Modifier.height(38.dp).testTag("undo_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                }
            }

            // Manager Simulation Bar
            Text(
                text = "SIMULATION ENGINE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSimulateBall,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("simulate_ball_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ball", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onSimulateOver,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("simulate_over_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Over", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onAutoSimulate,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(42.dp)
                        .testTag("auto_simulate_innings_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Innings >>", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CommentaryItem(ball: BallEventEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ball.isWicket) Color(0xFFFFEBEE)
            else if (ball.runsBat >= 4) Color(0xFFE8F5E9)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(44.dp)) {
                Text(
                    text = "${ball.overNumber}.${ball.ballNumberInOver}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CricketGreenDark
                )
            }
            BallChip(label = ball.shortLabel, isWicket = ball.isWicket, runs = ball.runsBat)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = ball.commentary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MatchCompletedCard(
    match: MatchEntity,
    onViewScorecard: () -> Unit,
    onStartNewMatch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Match Over",
                tint = CricketGold,
                modifier = Modifier.size(54.dp)
            )
            Text(
                text = "MATCH CONCLUDED",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8D6E63)
            )
            Text(
                text = match.resultDescription,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = CricketGreenDark
            )
            Text(
                text = "Innings 1: ${match.inn1Runs}/${match.inn1Wickets} • Innings 2: ${match.inn2Runs}/${match.inn2Wickets}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewScorecard,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
                ) {
                    Text("Full Scorecard")
                }
                OutlinedButton(
                    onClick = onStartNewMatch,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next Match")
                }
            }
        }
    }
}

@Composable
fun WicketDialog(
    onDismiss: () -> Unit,
    onConfirmWicket: (WicketType) -> Unit
) {
    val wicketTypes = listOf(
        WicketType.CAUGHT to "Caught",
        WicketType.BOWLED to "Bowled",
        WicketType.LBW to "LBW",
        WicketType.RUN_OUT to "Run Out",
        WicketType.STUMPED to "Stumped",
        WicketType.HIT_WICKET to "Hit Wicket"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Dismissal Type", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                wicketTypes.forEach { (type, label) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onConfirmWicket(type) },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TacticsDialog(
    currentMindset: BattingMindset,
    currentPlan: BowlingPlan,
    onDismiss: () -> Unit,
    onSave: (BattingMindset, BowlingPlan) -> Unit
) {
    var selectedMindset by remember { mutableStateOf(currentMindset) }
    var selectedPlan by remember { mutableStateOf(currentPlan) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Manager Tactical Plans", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text(
                        text = "Batting Intent & Mindset:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(BattingMindset.entries.toTypedArray()) { m ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMindset = m },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedMindset == m) CricketGreenPrimary.copy(alpha = 0.15f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedMindset == m) CricketGreenPrimary else Color.LightGray
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = m.displayName, fontWeight = FontWeight.Bold)
                            Text(text = m.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bowling Line & Strategy:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(BowlingPlan.entries.toTypedArray()) { p ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = p },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedPlan == p) CricketOrange.copy(alpha = 0.15f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedPlan == p) CricketOrange else Color.LightGray
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = p.displayName, fontWeight = FontWeight.Bold)
                            Text(text = p.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedMindset, selectedPlan) },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary)
            ) {
                Text("Apply Tactics")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
