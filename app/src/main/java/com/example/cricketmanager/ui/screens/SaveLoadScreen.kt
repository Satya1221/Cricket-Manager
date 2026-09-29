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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.SaveGameSlot
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun SaveLoadScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val saveSlots by viewModel.saveSlots.collectAsState()
    var statusMessage by remember { mutableStateOf<String?>(null) }

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
                        text = "SAVE & LOAD CAREER",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Multiple save slots & career backups",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = CricketGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "PERSISTENT GAME ENGINE", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Your progress, matches, finances and wonderkids are stored locally.", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Career Save Slots")
        }

        items(saveSlots) { slot ->
            SaveSlotCard(
                slot = slot,
                onSave = {
                    viewModel.saveCareer(slot.slotId)
                    statusMessage = "Franchise saved to Slot ${slot.slotId}!"
                },
                onLoad = {
                    viewModel.loadCareer(slot.slotId)
                    statusMessage = "Loaded career from Slot ${slot.slotId}!"
                }
            )
        }
    }

    if (statusMessage != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { statusMessage = null }) {
                    Text("OK", color = CricketGold)
                }
            },
            containerColor = CricketNavySurfaceElevated,
            contentColor = TextPrimary
        ) {
            Text(statusMessage ?: "")
        }
    }
}

@Composable
fun SaveSlotCard(
    slot: SaveGameSlot,
    onSave: () -> Unit,
    onLoad: () -> Unit
) {
    PremiumCard(
        borderColor = if (slot.isOccupied) CricketGoldBorder else Color(0x22FFD700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = slot.slotTitle, color = CricketGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = slot.lastSavedTimestamp, color = TextMuted, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (slot.isOccupied) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = slot.teamName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Season ${slot.seasonNumber} • ${slot.inGameDate}", color = TextSecondary, fontSize = 12.sp)
                }
                Text(text = "Purse: ${slot.purseFormatted}", color = CricketGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(text = "Empty Save Slot", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GoldButton(
                text = "SAVE TO SLOT ${slot.slotId}",
                onClick = onSave,
                modifier = Modifier.weight(1f),
                height = 36.dp
            )

            PremiumButton(
                text = "LOAD SLOT",
                onClick = onLoad,
                enabled = slot.isOccupied,
                modifier = Modifier.weight(1f),
                height = 36.dp
            )
        }
    }
}
