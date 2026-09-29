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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TrainingScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Batting", "Bowling", "Fielding", "Physical")

    val teamPlayers by viewModel.teamPlayers.collectAsState()
    var trainedPlayerName by remember { mutableStateOf<String?>(null) }

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
                        text = "TRAINING COMPLEX",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Upgrade squad attributes & fitness",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Training Tab Selector
        item {
            TabSelector(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        item {
            val discipline = tabs[selectedTab]
            SectionHeader(
                title = "$discipline Drills",
                subtitle = "Select player to assign individual coaching regime"
            )
        }

        items(teamPlayers) { player ->
            val discipline = tabs[selectedTab]
            val skillValue = when (discipline) {
                "Batting" -> player.battingSkill
                "Bowling" -> player.bowlingSkill
                "Fielding" -> player.fieldingSkill
                else -> ((player.battingSkill + player.bowlingSkill) / 2)
            }

            TrainingPlayerRow(
                player = player,
                discipline = discipline,
                skillValue = skillValue,
                onTrainClick = {
                    viewModel.trainPlayer(player, discipline)
                    trainedPlayerName = "${player.name} ($discipline +1)"
                }
            )
        }
    }

    if (trainedPlayerName != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { trainedPlayerName = null }) {
                    Text("OK", color = CricketGold)
                }
            },
            containerColor = CricketNavySurfaceElevated,
            contentColor = TextPrimary
        ) {
            Text("Session Completed: $trainedPlayerName")
        }
    }
}

@Composable
fun TrainingPlayerRow(
    player: PlayerEntity,
    discipline: String,
    skillValue: Int,
    onTrainClick: () -> Unit
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
            // Player portrait
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF202A4E))
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsCricket,
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
                    text = "${player.role.name.replace("_", " ")} • Overall $overallRating",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            RatingBadge(rating = skillValue, size = 36.dp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        StatBar(
            label = "$discipline Rating",
            value = skillValue,
            maxValue = 99,
            fillBrush = GoldGradient
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoldButton(
            text = "TRAIN $discipline.uppercase()",
            onClick = onTrainClick,
            modifier = Modifier.fillMaxWidth(),
            height = 36.dp,
            icon = Icons.Default.FitnessCenter
        )
    }
}
