package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerInjury
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun MedicalScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val injuries by viewModel.injuries.collectAsState()
    val allPlayers by viewModel.teamPlayers.collectAsState()

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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SPORTS SCIENCE & REHAB",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Squad Fitness, Injuries & Recovery",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Squad Readiness Meters
        item {
            PremiumCard(borderColor = CricketGoldBorder, modifier = Modifier.fillMaxWidth()) {
                Text(text = "SQUAD CONDITION OVERVIEW", color = CricketGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                StatBar(label = "Average Squad Fitness", value = 92, maxValue = 100, fillBrush = GoldGradient)
                Spacer(modifier = Modifier.height(6.dp))
                StatBar(label = "Fatigue Suppression", value = 84, maxValue = 100, fillBrush = GoldGradient)
            }
        }

        item {
            SectionHeader(
                title = "Medical Ward",
                subtitle = "${injuries.size} Players currently in rehabilitation"
            )
        }

        if (injuries.isEmpty()) {
            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CricketStadiumGreen, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "CLEAN BILL OF HEALTH", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "No active squad injuries. All athletes available for Playing XI selection.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(injuries) { injury ->
                val player = allPlayers.find { it.id == injury.playerId }
                InjuryCardRow(injury = injury, playerName = player?.name ?: "Squad Athlete #${injury.playerId}")
            }
        }

        item {
            SectionHeader(title = "Rehab Protocols", subtitle = "Physio staff treatments")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RehabProtocolRow(
                    title = "Cryotherapy & Cold Plunge",
                    effect = "-1 Day Injury Duration • Speeds soft tissue recovery",
                    icon = Icons.Default.AcUnit
                )
                RehabProtocolRow(
                    title = "Hyperbaric Oxygen Therapy",
                    effect = "+5% Matchday Stamina • Restores cellular oxygenation",
                    icon = Icons.Default.Air
                )
                RehabProtocolRow(
                    title = "Biomechanical Load Monitoring",
                    effect = "-40% Strain Risk for Fast Bowlers during death overs",
                    icon = Icons.Default.MonitorHeart
                )
            }
        }
    }
}

@Composable
fun InjuryCardRow(
    injury: PlayerInjury,
    playerName: String
) {
    PremiumCard(
        borderColor = CricketRedAccent,
        borderWidth = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF381216)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = CricketRedAccent, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = playerName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = "${injury.injuryName} (${injury.severity})", color = CricketRedAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Rehab Countdown: ${injury.daysRemaining} days remaining", color = TextSecondary, fontSize = 11.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF381216))
                    .border(BorderStroke(1.dp, CricketRedAccent), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${injury.daysRemaining}D OUT",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun RehabProtocolRow(
    title: String,
    effect: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketNavySurfaceElevated)
            .border(BorderStroke(0.5.dp, Color(0x33FFD700)), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CricketNavyCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = effect, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
