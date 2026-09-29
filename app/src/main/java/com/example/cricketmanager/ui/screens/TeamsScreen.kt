package com.example.cricketmanager.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TeamsScreen(
    viewModel: CricketViewModel,
    onTeamSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.allTeams.collectAsState()
    var showAddTeamDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTeamDialog = true },
                containerColor = CricketGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_custom_team_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Team")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cricket Franchises",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Manage squads, tactics, captains, and player ratings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(teams) { team ->
                TeamCardItem(
                    team = team,
                    onClick = { onTeamSelected(team.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddTeamDialog) {
        CreateTeamDialog(
            onDismiss = { showAddTeamDialog = false },
            onConfirm = { name, code, city, primaryColor, secondaryColor ->
                showAddTeamDialog = false
                viewModel.createTeam(name, code, city, primaryColor, secondaryColor)
            }
        )
    }
}

@Composable
fun TeamCardItem(
    team: TeamEntity,
    onClick: () -> Unit
) {
    val primaryColor = try {
        Color(android.graphics.Color.parseColor(team.primaryColorHex))
    } catch (_: Exception) {
        CricketGreenPrimary
    }
    val secondaryColor = try {
        Color(android.graphics.Color.parseColor(team.secondaryColorHex))
    } catch (_: Exception) {
        Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("team_card_${team.shortCode}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge / Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = team.shortCode.take(3),
                    color = secondaryColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (team.isCustom) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "CUSTOM",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stadium,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${team.city} • ${team.homeGround}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Record: ${team.matchesWon}W - ${team.matchesLost}L (${team.matchesPlayed} Matches)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = CricketGreenPrimary
                )
            }
        }
    }
}

@Composable
fun CreateTeamDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, shortCode: String, city: String, primary: String, secondary: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shortCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val presetColors = listOf(
        "#1B5E20" to "#FFD700", // Green & Gold
        "#0D47A1" to "#E0E0E0", // Blue & Silver
        "#B71C1C" to "#000000", // Red & Black
        "#4A148C" to "#FFD700", // Purple & Gold
        "#E65100" to "#212121", // Orange & Black
        "#006064" to "#80DEEA"  // Teal & Cyan
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Team", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Team Name (e.g. Apex Predators)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("team_name_input")
                )
                OutlinedTextField(
                    value = shortCode,
                    onValueChange = { if (it.length <= 4) shortCode = it.uppercase() },
                    label = { Text("Short Code (e.g. APX)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("team_code_input")
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City / Region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Team Kit Colors:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEachIndexed { index, (pri, sec) ->
                        val color = try { Color(android.graphics.Color.parseColor(pri)) } catch (_: Exception) { Color.Green }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorIndex = index }
                                .padding(if (selectedColorIndex == index) 4.dp else 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColorIndex == index) {
                                Surface(
                                    modifier = Modifier.size(12.dp),
                                    shape = CircleShape,
                                    color = Color.White
                                ) {}
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && shortCode.isNotBlank()) {
                        val (pri, sec) = presetColors[selectedColorIndex]
                        onConfirm(name, shortCode, city.ifBlank { "Home City" }, pri, sec)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricketGreenPrimary),
                modifier = Modifier.testTag("confirm_create_team_btn")
            ) {
                Text("Create Team")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
