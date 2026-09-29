package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerRole
import com.example.cricketmanager.data.model.YouthPlayer
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun YouthAcademyScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val youthPlayers by viewModel.youthPlayers.collectAsState()
    val academyLevel by viewModel.academyLevel.collectAsState()
    val userPurse by viewModel.userPurseLakhs.collectAsState()
    val userTeamId by viewModel.userTeamId.collectAsState()

    var showPromotedSuccess by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Academy Header Card
        item {
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = CricketGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "YOUTH ACADEMY",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Tier $academyLevel Facility • Next-Gen Stars",
                            color = CricketGoldLight,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF382900), Color(0xFF1B1400))))
                            .border(BorderStroke(1.dp, CricketGold), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Level $academyLevel / 5",
                            color = CricketGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Nurture raw talent into world-class franchise stars. Train youth recruits to unlock their maximum potential and promote them directly to the senior Playing XI squad.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoldButton(
                        text = "UPGRADE ACADEMY (₹2 Cr)",
                        onClick = { viewModel.upgradeAcademy() },
                        enabled = academyLevel < 5 && userPurse >= 200,
                        modifier = Modifier.weight(1f),
                        height = 38.dp,
                        icon = Icons.Default.Upgrade
                    )
                }
            }
        }

        // Section: Academy Prospects
        item {
            SectionHeader(
                title = "Emerging Prospects",
                subtitle = "${youthPlayers.filter { !it.isPromoted }.size} Players in Development"
            )
        }

        items(youthPlayers) { youth ->
            YouthPlayerCard(
                youth = youth,
                onTrain = { viewModel.trainYouthPlayer(youth.id) },
                onPromote = {
                    viewModel.promoteYouthToSenior(youth.id, userTeamId)
                    showPromotedSuccess = "${youth.name} has been promoted to the senior squad!"
                }
            )
        }

        // Development Camps
        item {
            SectionHeader(
                title = "Academy Training Camps",
                subtitle = "Coaching modules"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AcademyCampRow(
                    title = "Express Pace & Seam Camp",
                    coach = "Brett Lee Masterclass",
                    focus = "+3 Bowling Skill, +4 Stamina",
                    icon = Icons.Default.Bolt
                )
                AcademyCampRow(
                    title = "Power Hitting & 360° Batting",
                    coach = "AB De Villiers Clinic",
                    focus = "+4 Power Hitting, +3 Boundary %",
                    icon = Icons.Default.SportsCricket
                )
                AcademyCampRow(
                    title = "Mystery Spin & Control Lab",
                    coach = "Anil Kumble Academy",
                    focus = "+3 Turn & Drift, +4 Dot Ball %",
                    icon = Icons.Default.Sync
                )
            }
        }
    }

    if (showPromotedSuccess != null) {
        AlertDialog(
            onDismissRequest = { showPromotedSuccess = null },
            confirmButton = {
                GoldButton(
                    text = "EXCELLENT",
                    onClick = { showPromotedSuccess = null }
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = CricketGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Promotion Confirmed!", color = TextPrimary)
                }
            },
            text = {
                Text(showPromotedSuccess ?: "", color = TextSecondary)
            },
            containerColor = CricketNavySurfaceElevated
        )
    }
}

@Composable
fun YouthPlayerCard(
    youth: YouthPlayer,
    onTrain: () -> Unit,
    onPromote: () -> Unit
) {
    PremiumCard(
        borderColor = if (youth.isPromoted) Color(0xFF2E3B5F) else Color(0x44FFD700),
        backgroundColor = if (youth.isPromoted) Color(0xFF10162B) else CricketNavyCard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E284A))
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = CricketGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AGE ${youth.age}",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = youth.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (youth.isPromoted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketStadiumGreen)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PROMOTED",
                                color = Color.Black,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Text(
                    text = "${youth.role.name.replace("_", " ")} • ${youth.specialty}",
                    color = CricketGoldLight,
                    fontSize = 11.sp
                )
                Text(
                    text = "Bat: ${youth.battingSkill} | Bowl: ${youth.bowlingSkill} | Potential: ${youth.potentialSkill}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                RatingBadge(rating = youth.currentSkill, size = 38.dp)
                Text(
                    text = "POT ${youth.potentialSkill}",
                    color = CricketStadiumNeon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress to next upgrade
        StatBar(
            label = "Development Progress",
            value = (youth.progress * 100).toInt(),
            maxValue = 100,
            fillBrush = GoldGradient
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!youth.isPromoted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PremiumButton(
                    text = "DRILL TRAIN",
                    onClick = onTrain,
                    modifier = Modifier.weight(1f),
                    height = 36.dp,
                    icon = Icons.Default.FitnessCenter
                )

                GoldButton(
                    text = "PROMOTE TO SENIOR",
                    onClick = onPromote,
                    modifier = Modifier.weight(1f),
                    height = 36.dp,
                    icon = Icons.Default.ArrowUpward
                )
            }
        }
    }
}

@Composable
fun AcademyCampRow(
    title: String,
    coach: String,
    focus: String,
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
                .background(CricketNavyCard)
                .border(BorderStroke(1.dp, CricketGoldBorder), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = coach, color = CricketGoldLight, fontSize = 11.sp)
            Text(text = focus, color = TextMuted, fontSize = 10.sp)
        }
    }
}
