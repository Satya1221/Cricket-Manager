package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cricketmanager.ui.components.GoldButton
import com.example.cricketmanager.ui.components.TeamBadge
import com.example.cricketmanager.ui.theme.*

@Composable
fun NewMatchDialog(
    teams: List<TeamEntity>,
    onDismiss: () -> Unit,
    onConfirm: (team1Id: Long, team2Id: Long, overs: Int, tossWinnerId: Long, tossDecision: TossDecision) -> Unit
) {
    if (teams.size < 2) return

    var team1Index by remember { mutableStateOf(0) }
    var team2Index by remember { mutableStateOf(1) }
    var selectedOvers by remember { mutableStateOf(20) }
    var tossWinnerIndex by remember { mutableStateOf(0) }
    var tossDecision by remember { mutableStateOf(TossDecision.BAT) }

    val team1 = teams[team1Index]
    val team2 = teams[team2Index]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "START NEW MATCH",
                color = CricketGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(text = "Select Franchises", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Team 1
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            TeamBadge(shortCode = team1.shortCode, primaryColorHex = team1.primaryColorHex, size = 42.dp)
                            Text(text = team1.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "VS", color = CricketGold, fontWeight = FontWeight.Black)
                        // Team 2
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            TeamBadge(shortCode = team2.shortCode, primaryColorHex = team2.primaryColorHex, size = 42.dp)
                            Text(text = team2.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text(text = "Overs Per Innings", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 20).forEach { ov ->
                            val isSel = selectedOvers == ov
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (isSel) Modifier.background(GoldGradient)
                                        else Modifier.background(CricketNavyCard)
                                    )
                                    .border(1.dp, if (isSel) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                                    .clickable { selectedOvers = ov }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$ov Overs",
                                    color = if (isSel) Color.Black else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(text = "Toss Winner", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(team1, team2).forEachIndexed { i, t ->
                            val isSel = tossWinnerIndex == i
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CricketNavySurfaceElevated else CricketNavyCard)
                                    .border(1.dp, if (isSel) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                                    .clickable { tossWinnerIndex = i }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t.shortCode,
                                    color = if (isSel) CricketGoldLight else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(text = "Toss Decision", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(TossDecision.BAT, TossDecision.BOWL).forEach { dec ->
                            val isSel = tossDecision == dec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CricketNavySurfaceElevated else CricketNavyCard)
                                    .border(1.dp, if (isSel) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                                    .clickable { tossDecision = dec }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dec.name,
                                    color = if (isSel) CricketGoldLight else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            GoldButton(
                text = "START MATCH",
                onClick = {
                    val tossWinnerId = if (tossWinnerIndex == 0) team1.id else team2.id
                    onConfirm(team1.id, team2.id, selectedOvers, tossWinnerId, tossDecision)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        },
        containerColor = CricketNavySurfaceElevated
    )
}
