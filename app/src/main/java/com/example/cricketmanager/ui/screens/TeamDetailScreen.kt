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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.PlayerRole
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.ui.theme.CricketOrange
import com.example.cricketmanager.viewmodel.CricketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    viewModel: CricketViewModel,
    teamId: Long,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val team by viewModel.selectedTeam.collectAsState()
    val players by viewModel.teamPlayers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Playing XI, 1 = Full Squad
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    val primaryColor = try {
        Color(android.graphics.Color.parseColor(team?.primaryColorHex ?: "#125C44"))
    } catch (_: Exception) {
        CricketGreenPrimary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team?.name ?: "Team Squad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("team_detail_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPlayerDialog = true },
                containerColor = primaryColor,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_player_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Player")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Team Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = team?.shortCode ?: "TM",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = team?.name ?: "Team",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Home: ${team?.homeGround} • ${team?.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Record: ${team?.matchesWon} Won / ${team?.matchesLost} Lost",
                            style = MaterialTheme.typography.bodySmall,
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tabs
            val playingXi = players.filter { it.inPlayingXi }
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = primaryColor
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Playing XI (${playingXi.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Full Squad (${players.size})", fontWeight = FontWeight.Bold) }
                )
            }

            val displayPlayers = if (selectedTab == 0) playingXi else players

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayPlayers) { player ->
                    PlayerCardItem(
                        player = player,
                        onToggleXi = { viewModel.togglePlayingXi(player) },
                        onMakeCaptain = { viewModel.setCaptain(player) },
                        onMakeKeeper = { viewModel.setWicketKeeper(player) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showAddPlayerDialog && team != null) {
        AddPlayerDialog(
            teamId = team!!.id,
            onDismiss = { showAddPlayerDialog = false },
            onConfirm = { name, role, batSkill, bowlSkill, jersey ->
                showAddPlayerDialog = false
                viewModel.addPlayer(team!!.id, name, role, batSkill, bowlSkill, jersey)
            }
        )
    }
}

@Composable
fun PlayerCardItem(
    player: PlayerEntity,
    onToggleXi: () -> Unit,
    onMakeCaptain: () -> Unit,
    onMakeKeeper: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (player.inPlayingXi) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (player.inPlayingXi) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Jersey circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CricketGreenDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${player.jerseyNumber}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (player.isCaptain) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = CricketGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "CAPTAIN",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                        if (player.isWicketKeeper) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = CricketOrange,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "WK",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = "${player.role.name.replace("_", " ")} • ${player.battingHand.name.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Playing XI badge
                FilterChip(
                    selected = player.inPlayingXi,
                    onClick = onToggleXi,
                    label = { Text(if (player.inPlayingXi) "In XI" else "Bench") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Skill Progress Bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Batting Skill", style = MaterialTheme.typography.labelSmall)
                        Text("${player.battingSkill}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { player.battingSkill / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = CricketGreenPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bowling Skill", style = MaterialTheme.typography.labelSmall)
                        Text("${player.bowlingSkill}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { player.bowlingSkill / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = CricketOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions row: Set Captain / Set Keeper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!player.isCaptain) {
                    TextButton(onClick = onMakeCaptain) {
                        Text("Set Captain", style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (!player.isWicketKeeper && player.role != PlayerRole.BOWLER) {
                    TextButton(onClick = onMakeKeeper) {
                        Text("Set Keeper", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlayerDialog(
    teamId: Long,
    onDismiss: () -> Unit,
    onConfirm: (name: String, role: PlayerRole, battingSkill: Int, bowlingSkill: Int, jersey: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(PlayerRole.BATSMAN) }
    var battingSkill by remember { mutableFloatStateOf(75f) }
    var bowlingSkill by remember { mutableFloatStateOf(60f) }
    var jerseyNumber by remember { mutableStateOf("18") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Player to Squad", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("player_name_input")
                )
                OutlinedTextField(
                    value = jerseyNumber,
                    onValueChange = { if (it.length <= 3) jerseyNumber = it.filter { c -> c.isDigit() } },
                    label = { Text("Jersey Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Role:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PlayerRole.entries.forEach { r ->
                        FilterChip(
                            selected = selectedRole == r,
                            onClick = { selectedRole = r },
                            label = { Text(r.name.take(3)) }
                        )
                    }
                }

                Text("Batting Skill: ${battingSkill.toInt()}", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = battingSkill,
                    onValueChange = { battingSkill = it },
                    valueRange = 40f..99f
                )

                Text("Bowling Skill: ${bowlingSkill.toInt()}", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = bowlingSkill,
                    onValueChange = { bowlingSkill = it },
                    valueRange = 30f..99f
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name,
                            selectedRole,
                            battingSkill.toInt(),
                            bowlingSkill.toInt(),
                            jerseyNumber.toIntOrNull() ?: 10
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                modifier = Modifier.testTag("confirm_add_player_btn")
            ) {
                Text("Add Player")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
