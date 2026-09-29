package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
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
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun SettingsScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var difficulty by remember { mutableStateOf("Professional") }
    var soundEnabled by remember { mutableStateOf(true) }
    var graphicsQuality by remember { mutableStateOf("Ultra 3D") }
    var commentarySpeed by remember { mutableStateOf("Normal (1.0x)") }
    var showResetDialog by remember { mutableStateOf(false) }

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
                        text = "GAME SETTINGS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Simulation & Preferences",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Match Simulation", subtitle = "Game engine parameters")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingRow(title = "Difficulty Level", value = difficulty, icon = Icons.Default.Speed) {
                    difficulty = when (difficulty) {
                        "Standard" -> "Professional"
                        "Professional" -> "World Class"
                        else -> "Standard"
                    }
                }
                SettingRow(title = "3D Graphics Quality", value = graphicsQuality, icon = Icons.Default.VideogameAsset) {
                    graphicsQuality = when (graphicsQuality) {
                        "Medium" -> "High"
                        "High" -> "Ultra 3D"
                        else -> "Medium"
                    }
                }
                SettingRow(title = "Commentary Feed Speed", value = commentarySpeed, icon = Icons.Default.GraphicEq) {
                    commentarySpeed = when (commentarySpeed) {
                        "Fast (1.5x)" -> "Ultra (2.0x)"
                        "Ultra (2.0x)" -> "Normal (1.0x)"
                        else -> "Fast (1.5x)"
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Audio & Sound FX", subtitle = "Atmospheric ground sounds")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CricketNavySurfaceElevated)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = CricketGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Stadium & Crowd Sound FX", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Bat strikes, appeal shouts & crowd roar", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CricketGold, checkedTrackColor = Color(0xFF4A3800))
                )
            }
        }

        item {
            SectionHeader(title = "Career Management", subtitle = "Franchise progression")
            PremiumButton(
                text = "RESET CAREER & DATA",
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                borderColor = CricketRedAccent,
                textColor = CricketRedAccent,
                icon = Icons.Default.DeleteForever
            )
        }

        item {
            SectionHeader(title = "About", subtitle = "Version & credits")
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cricket Master Manager", color = CricketGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Version 2.4.0 (3D Simulation Edition)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Professional cricket management simulator with 3D perspective pitch engine, ball-by-ball physics, youth academy, and live seasonal player auctions.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                GoldButton(
                    text = "CONFIRM RESET",
                    onClick = {
                        viewModel.advanceToNextSeason()
                        showResetDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            title = { Text("Reset Career Progress?", color = TextPrimary) },
            text = { Text("This will reset your tournament progression, finances and start a fresh career season with the Mega Auction.", color = TextSecondary) },
            containerColor = CricketNavySurfaceElevated
        )
    }
}

@Composable
fun SettingRow(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketNavySurfaceElevated)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, color = CricketGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}
