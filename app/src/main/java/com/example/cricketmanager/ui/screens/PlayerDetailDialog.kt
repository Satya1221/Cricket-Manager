package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*

@Composable
fun PlayerDetailDialog(
    player: PlayerEntity,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Stats", "Attributes")

    val overallRating = ((player.battingSkill + player.bowlingSkill + player.fieldingSkill) / 3)
    val starCount = (overallRating / 20).coerceIn(3, 5)

    val marketValue = "₹${String.format("%.2f", overallRating * 0.18)} Cr"
    val basePrice = "₹${String.format("%.2f", overallRating * 0.10)} Cr"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GoldButton(
                text = "CLOSE PROFILE",
                onClick = onDismiss,
                height = 42.dp
            )
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Profile Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF2E3D6E), Color(0xFF131A36))))
                            .border(BorderStroke(1.5.dp, CricketGold), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.SportsCricket, contentDescription = null, tint = CricketGold, modifier = Modifier.size(30.dp))
                            Text(text = "#${player.jerseyNumber}", color = CricketGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = player.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${player.role.name.replace("_", " ")} • India", color = CricketGoldLight, fontSize = 12.sp)

                        // Stars row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(starCount) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = CricketGold, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    RatingBadge(rating = overallRating, size = 46.dp)
                }

                // Tabs
                TabSelector(
                    tabs = tabs,
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                when (selectedTab) {
                    0 -> {
                        // Overview Tab
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Batting Style", player.battingHand.name.replace("_", " "))
                            InfoRow("Bowling Style", player.bowlingStyle.name.replace("_", " "))
                            InfoRow("Base Price", basePrice)
                            InfoRow("Current Valuation", marketValue)
                            InfoRow("Lineup Status", if (player.inPlayingXi) "Playing XI (Starter)" else "Squad Bench")
                        }
                    }
                    1 -> {
                        // Stats Tab
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Matches Played", "${player.matchesPlayed}")
                            InfoRow("Runs Scored", "${player.runsScored}")
                            InfoRow("Highest Score", "${player.highestScore}")
                            InfoRow("Centuries / 50s", "${player.centuries} / ${player.halfCenturies}")
                            InfoRow("Wickets Taken", "${player.wicketsTaken}")
                            InfoRow("Batting Average", String.format("%.2f", player.battingAverage))
                        }
                    }
                    2 -> {
                        // Attributes Tab
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatBar(label = "Batting Skill", value = player.battingSkill, fillBrush = GoldGradient)
                            StatBar(label = "Bowling Skill", value = player.bowlingSkill, fillBrush = GoldGradient)
                            StatBar(label = "Fielding Reflexes", value = player.fieldingSkill, fillBrush = GoldGradient)
                            StatBar(label = "Stamina & Physical", value = 82, fillBrush = GoldGradient)
                            StatBar(label = "Clutch Composure", value = 86, fillBrush = GoldGradient)
                        }
                    }
                }
            }
        },
        containerColor = CricketNavySurfaceElevated
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CricketNavyCard)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
