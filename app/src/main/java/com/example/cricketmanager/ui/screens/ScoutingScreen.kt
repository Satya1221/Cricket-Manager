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
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.PlayerRole
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun ScoutingScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Batsman", "Bowler", "All Rounder")

    val topBatters by viewModel.topRunScorers.collectAsState()
    val topBowlers by viewModel.topWicketTakers.collectAsState()
    val allScouted = remember(topBatters, topBowlers) {
        (topBatters + topBowlers).distinctBy { it.id }
    }

    val filtered = when (selectedTab) {
        1 -> allScouted.filter { it.role == PlayerRole.BATSMAN || it.role == PlayerRole.WICKET_KEEPER }
        2 -> allScouted.filter { it.role == PlayerRole.BOWLER }
        3 -> allScouted.filter { it.role == PlayerRole.ALL_ROUNDER }
        else -> allScouted
    }

    var signedPlayerDialog by remember { mutableStateOf<PlayerEntity?>(null) }

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
                        text = "GLOBAL SCOUTING NETWORK",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scout & acquire verified league talent",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            TabSelector(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        item {
            SectionHeader(
                title = "Scouted Prospects",
                subtitle = "${filtered.size} Players identified by analytics department"
            )
        }

        items(filtered) { player ->
            ScoutedPlayerCard(
                player = player,
                onSignInterest = { signedPlayerDialog = player }
            )
        }
    }

    if (signedPlayerDialog != null) {
        val p = signedPlayerDialog!!
        AlertDialog(
            onDismissRequest = { signedPlayerDialog = null },
            confirmButton = {
                GoldButton(
                    text = "CONFIRM INQUIRY",
                    onClick = { signedPlayerDialog = null }
                )
            },
            dismissButton = {
                TextButton(onClick = { signedPlayerDialog = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            title = {
                Text("Scouting Inquiry: ${p.name}", color = TextPrimary)
            },
            text = {
                Text(
                    "Our scouting department has submitted a formal inquiry to register ${p.name} for the upcoming Season Mega Auction.",
                    color = TextSecondary
                )
            },
            containerColor = CricketNavySurfaceElevated
        )
    }
}

@Composable
fun ScoutedPlayerCard(
    player: PlayerEntity,
    onSignInterest: () -> Unit
) {
    val overallRating = ((player.battingSkill + player.bowlingSkill + player.fieldingSkill) / 3)

    PremiumCard(
        borderColor = Color(0x33FFD700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF222B4C))
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CricketGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${player.role.name.replace("_", " ")} • Runs: ${player.runsScored} • Wkts: ${player.wicketsTaken}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "Bat Skill: ${player.battingSkill} | Bowl Skill: ${player.bowlingSkill}",
                    color = CricketGoldLight,
                    fontSize = 11.sp
                )
            }

            RatingBadge(rating = overallRating, size = 38.dp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        PremiumButton(
            text = "SUBMIT AUCTION INQUIRY",
            onClick = onSignInterest,
            modifier = Modifier.fillMaxWidth(),
            height = 36.dp,
            icon = Icons.Default.Gavel
        )
    }
}
