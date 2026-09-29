package com.example.cricketmanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
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
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TeamDetailScreen(
    viewModel: CricketViewModel,
    teamId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    LaunchedEffect(teamId) {
        viewModel.selectTeam(teamId)
    }

    val team by viewModel.selectedTeam.collectAsState()
    val players by viewModel.teamPlayers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Playing XI", "Squad Bench")

    var viewingPlayer by remember { mutableStateOf<PlayerEntity?>(null) }

    val playingXi = players.filter { it.inPlayingXi }
    val bench = players.filter { !it.inPlayingXi }

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
                        text = team?.name ?: "Franchise Squad",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${playingXi.size} Playing XI • ${bench.size} Bench",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Team Header Card
        item {
            if (team != null) {
                val t = team!!
                PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TeamBadge(shortCode = t.shortCode, primaryColorHex = t.primaryColorHex, size = 52.dp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = t.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(text = "City: ${t.city} • Ground: ${t.homeGround}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Record: ${t.matchesWon} Won / ${t.matchesLost} Lost", color = CricketGoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Tab Selector
        item {
            TabSelector(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        val displayPlayers = if (selectedTab == 0) playingXi else bench

        items(displayPlayers) { player ->
            PlayerRow(
                player = player,
                onClick = { viewingPlayer = player },
                trailingAction = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { viewModel.togglePlayingXi(player) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (player.inPlayingXi) "BENCH" else "START",
                                color = CricketGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    }

    if (viewingPlayer != null) {
        PlayerDetailDialog(
            player = viewingPlayer!!,
            onDismiss = { viewingPlayer = null }
        )
    }
}
