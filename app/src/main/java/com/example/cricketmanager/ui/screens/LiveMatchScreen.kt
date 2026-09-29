package com.example.cricketmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.*
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
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
    val plan by viewModel.bowlingPlan.collectAsState()

    var showManualScorer by remember { mutableStateOf(false) }

    if (currentMatch == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CricketNavyBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF382900), Color(0xFF1B1400))))
                        .border(2.dp, CricketGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = null,
                        tint = CricketGold,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "NO MATCH IN PROGRESS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Launch a quick match or tournament fixture to experience real-time 3D pitch simulation, ball-by-ball commentary, and managerial coaching.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                GoldButton(
                    text = "START NEW 3D MATCH",
                    onClick = onStartNewMatchRequested,
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp,
                    icon = Icons.Default.PlayArrow
                )
            }
        }
        return
    }

    val match = currentMatch!!
    val team1 = allTeams.find { it.id == match.team1Id }
    val team2 = allTeams.find { it.id == match.team2Id }
    val battingTeam = if (match.battingTeamId == team1?.id) team1 else team2
    val bowlingTeam = if (battingTeam?.id == team1?.id) team2 else team1

    val striker = battingPlayers.find { it.id == match.currentStrikerId }
    val nonStriker = battingPlayers.find { it.id == match.currentNonStrikerId }
    val bowler = bowlingPlayers.find { it.id == match.currentBowlerId }

    val lastBall = matchBalls.lastOrNull()
    val currentOverBalls = remember(matchBalls) {
        val currentOver = match.currentBalls / 6
        matchBalls.filter { it.inningsNumber == match.currentInnings && it.overNumber == currentOver }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Match Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (match.status == MatchStatus.IN_PROGRESS) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CricketRedAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE INNINGS ${match.currentInnings}",
                            color = CricketRedAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    } else {
                        Text(
                            text = "MATCH FINISHED",
                            color = CricketGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PremiumButton(
                        text = "SCORECARD",
                        onClick = { onNavigateToScorecard(match.id) },
                        height = 32.dp,
                        icon = Icons.Default.Assessment
                    )
                }
            }
        }

        // Live Scoreboard Card
        item {
            PremiumCard(
                borderColor = CricketGold,
                borderWidth = 1.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Teams Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamBadge(
                            shortCode = battingTeam?.shortCode ?: "BAT",
                            primaryColorHex = battingTeam?.primaryColorHex ?: "#1E88E5",
                            size = 36.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = battingTeam?.name ?: "Batting Team",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (match.currentInnings == 2 && match.targetRuns != null) {
                        Text(
                            text = "TARGET: ${match.targetRuns}",
                            color = CricketGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big Score Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${match.currentRuns}/${match.currentWickets}",
                            color = CricketGold,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "(${match.currentOversFormatted}/${match.oversPerInnings} ov)",
                            color = TextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CRR: ${String.format("%.2f", match.currentRunRate)}",
                            color = CricketStadiumNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (match.currentInnings == 2) {
                            Text(
                                text = "RRR: ${String.format("%.2f", match.requiredRunRate)}",
                                color = CricketGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Batsmen on Crease & Current Bowler
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CricketNavySurfaceElevated)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Striker & Non-Striker
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${striker?.name ?: "Striker"} *",
                                color = CricketGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = nonStriker?.name ?: "Non-Striker",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Bowler
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(
                            text = bowler?.name ?: "Bowler",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bowling: ${plan.name.replace("_", " ")}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 3D CRICKET PITCH CANVAS
        item {
            Cricket3dPitchCanvas(
                lastBall = lastBall,
                isBowlingActive = match.status == MatchStatus.IN_PROGRESS
            )
        }

        // Current Over Balls Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "THIS OVER:",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (currentOverBalls.isEmpty()) {
                        item {
                            Text(text = "Over starting...", color = TextMuted, fontSize = 11.sp)
                        }
                    } else {
                        items(currentOverBalls) { b ->
                            BallBubble(ball = b)
                        }
                    }
                }
            }
        }

        // Latest Commentary Ticker
        item {
            if (lastBall != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CricketNavySurfaceElevated)
                        .border(0.5.dp, CricketGoldBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = CricketGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${lastBall.overNumber}.${lastBall.ballNumberInOver}: ${lastBall.commentary}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Match Completed Banner
        if (match.status == MatchStatus.COMPLETED) {
            item {
                PremiumCard(
                    borderColor = CricketGold,
                    borderWidth = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = CricketGold, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "MATCH CONCLUDED", color = CricketGoldLight, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = match.resultDescription,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PremiumButton(
                                text = "FULL SCORECARD",
                                onClick = { onNavigateToScorecard(match.id) },
                                modifier = Modifier.weight(1f)
                            )
                            GoldButton(
                                text = "NEW MATCH",
                                onClick = onStartNewMatchRequested,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Live Simulation Action Buttons
        if (match.status == MatchStatus.IN_PROGRESS) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Primary Simulation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GoldButton(
                            text = "SIMULATE BALL",
                            onClick = { viewModel.simulateNextBall() },
                            modifier = Modifier.weight(1.2f),
                            height = 50.dp,
                            icon = Icons.Default.SportsCricket,
                            testTag = "simulate_ball_btn"
                        )

                        PremiumButton(
                            text = "SIM OVER",
                            onClick = { viewModel.simulateCurrentOver() },
                            modifier = Modifier.weight(0.9f),
                            height = 50.dp,
                            icon = Icons.Default.FastForward,
                            testTag = "simulate_over_btn"
                        )

                        IconButton(
                            onClick = { viewModel.undoLastBall() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CricketNavySurfaceElevated)
                                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
                        ) {
                            Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo", tint = CricketGold)
                        }
                    }

                    // Secondary Fast Sim & Manual Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PremiumButton(
                            text = "AUTO-SIM INNINGS",
                            onClick = { viewModel.autoSimulateInnings() },
                            modifier = Modifier.weight(1f),
                            height = 38.dp,
                            icon = Icons.Default.PlayArrow
                        )

                        PremiumButton(
                            text = if (showManualScorer) "HIDE SCORER" else "MANUAL PAD",
                            onClick = { showManualScorer = !showManualScorer },
                            modifier = Modifier.weight(1f),
                            height = 38.dp,
                            icon = Icons.Default.Edit
                        )
                    }

                    // Manual Scoring Pad
                    AnimatedVisibility(visible = showManualScorer) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CricketNavySurfaceElevated)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Manual Scoring Buttons", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                            // Runs
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0, 1, 2, 3, 4, 6).forEach { runs ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (runs in listOf(4, 6)) GoldGradient else Brush.linearGradient(listOf(CricketNavyCard, CricketNavySurface)))
                                            .clickable {
                                                viewModel.recordManualBall(
                                                    runsBat = runs,
                                                    extraType = ExtraType.NONE,
                                                    extraRuns = 0,
                                                    isWicket = false,
                                                    wicketType = WicketType.NONE
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "$runs", color = if (runs in listOf(4, 6)) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Extras & Wicket
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.recordManualBall(0, ExtraType.WIDE, 1, false, WicketType.NONE)
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketStadiumBlue),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Wide", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.recordManualBall(0, ExtraType.NO_BALL, 1, false, WicketType.NONE)
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketStadiumPurple),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("NoBall", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        viewModel.recordManualBall(0, ExtraType.NONE, 0, true, WicketType.CAUGHT)
                                    },
                                    modifier = Modifier.weight(1f).height(34.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CricketRedAccent),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("OUT!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BallBubble(ball: BallEventEntity) {
    val (bgColor, textColor) = when {
        ball.isWicket -> CricketRedAccent to Color.White
        ball.runsBat == 6 -> CricketGold to Color.Black
        ball.runsBat == 4 -> CricketStadiumNeon to Color.Black
        ball.runsBat == 0 -> CricketNavySurfaceElevated to TextSecondary
        else -> CricketStadiumBlue to Color.White
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(0.5.dp, Color(0x55FFFFFF), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ball.shortLabel,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
