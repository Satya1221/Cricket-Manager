package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.TossDecision
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun MatchCentreScreen(
    viewModel: CricketViewModel,
    onPlayMatch: (Long, Long, Int, Long, TossDecision) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.allTeams.collectAsState()

    var selectedTeam1Index by remember { mutableStateOf(0) }
    var selectedTeam2Index by remember { mutableStateOf(1) }
    var matchOvers by remember { mutableStateOf(20) }

    var tossWinnerIndex by remember { mutableStateOf(0) }
    var tossDecision by remember { mutableStateOf(TossDecision.BAT) }

    val team1 = teams.getOrNull(selectedTeam1Index)
    val team2 = teams.getOrNull(selectedTeam2Index)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MATCH CENTRE",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Preview, Pitch Report & Toss Ceremony",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Teams VS Preview Banner Card
        item {
            if (team1 != null && team2 != null) {
                PremiumCard(
                    borderColor = CricketGold,
                    borderWidth = 1.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "T20 PREMIER LEAGUE FIXTURE", color = CricketGoldLight, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "7:30 PM IST", color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Team 1
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            TeamBadge(shortCode = team1.shortCode, primaryColorHex = team1.primaryColorHex, size = 56.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = team1.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = team1.city, color = TextSecondary, fontSize = 11.sp)
                        }

                        // VS
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CricketNavySurfaceElevated)
                                .border(1.dp, CricketGold, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "VS", color = CricketGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }

                        // Team 2
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            TeamBadge(shortCode = team2.shortCode, primaryColorHex = team2.primaryColorHex, size = 56.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = team2.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = team2.city, color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CricketNavySurfaceElevated)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Venue: ${team1.homeGround}",
                            color = CricketGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Pitch Report & Weather
        item {
            SectionHeader(title = "Pitch Report & Atmospheric Conditions", subtitle = "Curator pitch analysis")
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Clear Skies • 28°C", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "Humidity: 58% • Dew Factor: Medium", color = TextSecondary, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                StatBar(label = "Pace & Carry", value = 82, maxValue = 100, fillBrush = GoldGradient)
                Spacer(modifier = Modifier.height(6.dp))
                StatBar(label = "Spin & Turn", value = 64, maxValue = 100, fillBrush = GoldGradient)
                Spacer(modifier = Modifier.height(6.dp))
                StatBar(label = "Batting Track Quality", value = 88, maxValue = 100, fillBrush = GoldGradient)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "The pitch has a hard, true surface with generous carry for the quicks in the powerplay. Batting will be effortless under the lights with high true bounce.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Match Format & Overs
        item {
            SectionHeader(title = "Match Length", subtitle = "Choose overs per innings")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 20).forEach { ov ->
                    val isSelected = matchOvers == ov
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isSelected) Modifier.background(GoldGradient)
                                else Modifier.background(CricketNavySurfaceElevated)
                            )
                            .border(1.dp, if (isSelected) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                            .clickable { matchOvers = ov }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$ov OVERS",
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Toss Selector
        item {
            if (team1 != null && team2 != null) {
                SectionHeader(title = "Toss Decision", subtitle = "Simulate coin toss outcome")
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Toss Winner:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(team1, team2).forEachIndexed { index, t ->
                            val isSelected = tossWinnerIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CricketNavySurfaceElevated else CricketNavySurface)
                                    .border(1.dp, if (isSelected) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                                    .clickable { tossWinnerIndex = index }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t.name,
                                    color = if (isSelected) CricketGoldLight else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Elected To:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(TossDecision.BAT, TossDecision.BOWL).forEach { dec ->
                            val isSelected = tossDecision == dec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CricketNavySurfaceElevated else CricketNavySurface)
                                    .border(1.dp, if (isSelected) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                                    .clickable { tossDecision = dec }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dec.name,
                                    color = if (isSelected) CricketGoldLight else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // PLAY MATCH Action Button
        item {
            if (team1 != null && team2 != null) {
                GoldButton(
                    text = "PLAY MATCH (3D SIMULATION)",
                    onClick = {
                        val winnerId = if (tossWinnerIndex == 0) team1.id else team2.id
                        onPlayMatch(team1.id, team2.id, matchOvers, winnerId, tossDecision)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 54.dp,
                    icon = Icons.Default.SportsCricket
                )
            }
        }
    }
}
