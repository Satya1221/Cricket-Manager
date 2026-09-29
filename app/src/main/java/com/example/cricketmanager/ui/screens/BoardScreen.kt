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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.BoardObjective
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun BoardScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val objectives by viewModel.boardObjectives.collectAsState()
    val boardConf by viewModel.boardConfidence.collectAsState()
    val fanConf by viewModel.fanConfidence.collectAsState()

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
                        text = "BOARD & FAN RELATIONS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Franchise Mandate & Fan Sentiment",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Confidence Ratings Card
        item {
            PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                Text(text = "STAKEHOLDER CONFIDENCE RATINGS", color = CricketGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                StatBar(label = "Board of Directors Approval", value = boardConf, maxValue = 100, fillBrush = GoldGradient)
                Spacer(modifier = Modifier.height(8.dp))
                StatBar(label = "Fan Sentiment & Loyalty", value = fanConf, maxValue = 100, fillBrush = GoldGradient)
            }
        }

        item {
            SectionHeader(
                title = "Season Directives",
                subtitle = "Mandatory board objectives for season review"
            )
        }

        items(objectives) { obj ->
            BoardObjectiveRow(obj = obj)
        }
    }
}

@Composable
fun BoardObjectiveRow(obj: BoardObjective) {
    PremiumCard(
        borderColor = if (obj.isCompleted) CricketStadiumGreen else Color(0x33FFD700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (obj.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (obj.isCompleted) CricketStadiumGreen else TextMuted,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = obj.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CricketNavySurfaceElevated)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(text = obj.category, color = CricketGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = obj.targetDescription, color = TextSecondary, fontSize = 12.sp)
            }

            Text(
                text = "${obj.progressPercent}%",
                color = if (obj.isCompleted) CricketStadiumGreen else CricketGoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        StatBar(
            label = "Objective Progress",
            value = obj.progressPercent,
            maxValue = 100,
            fillBrush = GoldGradient
        )
    }
}
