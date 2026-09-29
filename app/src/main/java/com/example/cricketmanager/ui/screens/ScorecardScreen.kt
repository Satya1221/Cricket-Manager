package com.example.cricketmanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.ExtraType
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun ScorecardScreen(
    viewModel: CricketViewModel,
    matchId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    LaunchedEffect(matchId) {
        viewModel.selectMatch(matchId)
    }

    val match by viewModel.currentMatch.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()
    val balls by viewModel.matchBalls.collectAsState()
    val battingPlayers by viewModel.battingTeamPlayers.collectAsState()
    val bowlingPlayers by viewModel.bowlingTeamPlayers.collectAsState()

    var selectedInnings by remember { mutableStateOf(0) }
    val inningsNumber = selectedInnings + 1

    if (match == null) {
        Box(modifier = modifier.fillMaxSize().background(CricketNavyBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CricketGold)
        }
        return
    }

    val m = match!!
    val team1 = allTeams.find { it.id == m.team1Id }
    val team2 = allTeams.find { it.id == m.team2Id }

    val inningsBattingTeam = if (inningsNumber == 1) {
        if (m.battingFirstTeamId == team1?.id) team1 else team2
    } else {
        if (m.bowlingFirstTeamId == team1?.id) team1 else team2
    }

    val innBalls = balls.filter { it.inningsNumber == inningsNumber }
    val innRuns = if (inningsNumber == 1) m.inn1Runs else m.inn2Runs
    val innWickets = if (inningsNumber == 1) m.inn1Wickets else m.inn2Wickets
    val innBallsCount = if (inningsNumber == 1) m.inn1Balls else m.inn2Balls
    val innOvers = "${innBallsCount / 6}.${innBallsCount % 6}"

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CricketNavySurfaceElevated)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "MATCH SCORECARD", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${team1?.shortCode} vs ${team2?.shortCode}", color = CricketGoldLight, fontSize = 12.sp)
                }
            }
        }

        // Innings Tab Selector
        item {
            TabSelector(
                tabs = listOf(
                    "${team1?.shortCode ?: "Inn 1"} (1st)",
                    "${team2?.shortCode ?: "Inn 2"} (2nd)"
                ),
                selectedTabIndex = selectedInnings,
                onTabSelected = { selectedInnings = it }
            )
        }

        // Innings Summary Header
        item {
            PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = inningsBattingTeam?.name ?: "Innings $inningsNumber", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Innings $inningsNumber Total", color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(text = "$innRuns / $innWickets ($innOvers ov)", color = CricketGold, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Batting Table Header
        item {
            SectionHeader(title = "Batting Scorecard")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavySurfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "BATTER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "R", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
                Text(text = "B", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
                Text(text = "4s", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "6s", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "SR", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
            }
        }

        // Batting Rows
        val allParticipants = (battingPlayers + bowlingPlayers).distinctBy { it.id }
        val battersInInnings = innBalls.map { it.strikerId }.distinct()

        items(battersInInnings) { batterId ->
            val player = allParticipants.find { it.id == batterId }
            val batterBalls = innBalls.filter { it.strikerId == batterId }
            val runs = batterBalls.sumOf { it.runsBat }
            val legalBallsFaced = batterBalls.count { it.extraType != ExtraType.WIDE }
            val fours = batterBalls.count { it.runsBat == 4 }
            val sixes = batterBalls.count { it.runsBat == 6 }
            val sr = if (legalBallsFaced > 0) (runs.toDouble() / legalBallsFaced * 100).toInt() else 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavyCard)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player?.name ?: "Batter $batterId",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Text(text = "$runs", color = CricketGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
                Text(text = "$legalBallsFaced", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(26.dp))
                Text(text = "$fours", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                Text(text = "$sixes", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                Text(text = "$sr", color = CricketStadiumNeon, fontSize = 11.sp, modifier = Modifier.width(36.dp))
            }
        }

        // Bowling Table Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(title = "Bowling Analysis")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavySurfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "BOWLER", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = "O", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                Text(text = "M", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "R", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                Text(text = "W", color = CricketGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = "ECON", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
            }
        }

        val bowlersInInnings = innBalls.map { it.bowlerId }.distinct()

        items(bowlersInInnings) { bowlerId ->
            val player = allParticipants.find { it.id == bowlerId }
            val bowlerBalls = innBalls.filter { it.bowlerId == bowlerId }
            val legalBalls = bowlerBalls.count { it.isLegalBall }
            val runsConceded = bowlerBalls.sumOf { it.totalRuns }
            val wickets = bowlerBalls.count { it.isWicket }
            val oversStr = "${legalBalls / 6}.${legalBalls % 6}"
            val econ = if (legalBalls > 0) String.format("%.1f", runsConceded.toDouble() / (legalBalls / 6.0)) else "0.0"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavyCard)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player?.name ?: "Bowler $bowlerId",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Text(text = oversStr, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(28.dp))
                Text(text = "0", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                Text(text = "$runsConceded", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(28.dp))
                Text(text = "$wickets", color = CricketGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(text = econ, color = CricketStadiumNeon, fontSize = 11.sp, modifier = Modifier.width(36.dp))
            }
        }
    }
}
