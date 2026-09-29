package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun TacticsScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mindset by viewModel.battingMindset.collectAsState()
    val plan by viewModel.bowlingPlan.collectAsState()
    val fieldPreset by viewModel.fieldPreset.collectAsState()

    val presets = listOf("Balanced Ring", "Attacking Slips", "Death Overs Boundary", "Spin Web")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        text = "TACTICS & FIELD SETUP",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Field Placements & Strategy Mindset",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Cricket Field Visualizer
        item {
            PremiumCard(
                borderColor = CricketGoldBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "CRICKET GROUND FORMATION", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CricketNavySurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = fieldPreset.uppercase(), color = CricketGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FieldVisualizerCanvas(preset = fieldPreset)
            }
        }

        // Preset Selector
        item {
            SectionHeader(title = "Field Presets", subtitle = "Choose defensive or attacking formations")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { p ->
                    val isSelected = fieldPreset == p
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldGradient else Brush.linearGradient(listOf(CricketNavySurfaceElevated, CricketNavyCard)))
                            .border(0.5.dp, if (isSelected) CricketGold else Color(0x33FFD700), RoundedCornerShape(8.dp))
                            .clickable { viewModel.setFieldPreset(p) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.split(" ").first(),
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Batting Mindset
        item {
            SectionHeader(title = "Team Batting Aggression", subtitle = "Controls risk vs run rate in match simulation")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BattingMindset.entries.forEach { m ->
                    val isSelected = mindset == m
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CricketNavySurfaceElevated else CricketNavyCard)
                            .border(1.dp, if (isSelected) CricketGold else Color(0x22FFD700), RoundedCornerShape(10.dp))
                            .clickable { viewModel.setBattingMindset(m) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = m.name,
                                color = if (isSelected) CricketGoldLight else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (m) {
                                    BattingMindset.DEFENSIVE -> "Protect wickets, low boundary risk (CRR ~5.5)"
                                    BattingMindset.BALANCED -> "Steady strike rotation, controlled aggression (CRR ~8.0)"
                                    BattingMindset.AGGRESSIVE -> "Hunt boundaries, calculated aerial shots (CRR ~9.5)"
                                    BattingMindset.BLITZ -> "Maximum risk powerplay assault (CRR ~12.0+)"
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setBattingMindset(m) },
                            colors = RadioButtonDefaults.colors(selectedColor = CricketGold)
                        )
                    }
                }
            }
        }

        // Bowling Plan
        item {
            SectionHeader(title = "Bowling Strategy Plan", subtitle = "Delivery lines & wicket-taking tactics")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BowlingPlan.entries.forEach { p ->
                    val isSelected = plan == p
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CricketNavySurfaceElevated else CricketNavyCard)
                            .border(1.dp, if (isSelected) CricketGold else Color(0x22FFD700), RoundedCornerShape(10.dp))
                            .clickable { viewModel.setBowlingPlan(p) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = p.name.replace("_", " "),
                                color = if (isSelected) CricketGoldLight else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (p) {
                                    BowlingPlan.ATTACK_STUMPS -> "Full length target on stumps, high bowled/LBW chances"
                                    BowlingPlan.YORKER_BLITZ -> "Toe-crushers in death overs, suppresses boundaries"
                                    BowlingPlan.SHORT_BOUNCERS -> "Bouncers at helmet, forces hook & pull miscues"
                                    BowlingPlan.DEFENSIVE_WIDE -> "Tuck outside off stump, restricts big hitting"
                                    BowlingPlan.BALANCED -> "Standard line & length, adaptable"
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setBowlingPlan(p) },
                            colors = RadioButtonDefaults.colors(selectedColor = CricketGold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FieldVisualizerCanvas(preset: String) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val width = size.width
        val height = size.height

        // Ground Oval
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF226633), Color(0xFF144D25), Color(0xFF0C2B15)),
                center = Offset(width / 2, height / 2),
                radius = width * 0.48f
            ),
            topLeft = Offset(width * 0.04f, height * 0.04f),
            size = Size(width * 0.92f, height * 0.92f)
        )

        // 30-Yard Circle
        drawOval(
            color = Color(0x44FFFFFF),
            topLeft = Offset(width * 0.22f, height * 0.20f),
            size = Size(width * 0.56f, height * 0.60f),
            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )

        // Pitch Strip
        drawRect(
            color = PitchClay,
            topLeft = Offset(width * 0.46f, height * 0.35f),
            size = Size(width * 0.08f, height * 0.30f)
        )

        // Fielders based on preset
        val positions = when (preset) {
            "Attacking Slips" -> listOf(
                Offset(width * 0.58f, height * 0.65f) to "1st Slip",
                Offset(width * 0.64f, height * 0.68f) to "2nd Slip",
                Offset(width * 0.70f, height * 0.72f) to "Gully",
                Offset(width * 0.32f, height * 0.60f) to "Point",
                Offset(width * 0.30f, height * 0.44f) to "Cover",
                Offset(width * 0.40f, height * 0.28f) to "Mid-off",
                Offset(width * 0.60f, height * 0.28f) to "Mid-on",
                Offset(width * 0.72f, height * 0.45f) to "Mid-wicket",
                Offset(width * 0.50f, height * 0.30f) to "Bowler",
                Offset(width * 0.50f, height * 0.70f) to "Keeper",
                Offset(width * 0.82f, height * 0.72f) to "Fine Leg"
            )
            "Death Overs Boundary" -> listOf(
                Offset(width * 0.12f, height * 0.30f) to "Deep Cover",
                Offset(width * 0.14f, height * 0.65f) to "Deep Point",
                Offset(width * 0.35f, height * 0.12f) to "Long-off",
                Offset(width * 0.65f, height * 0.12f) to "Long-on",
                Offset(width * 0.88f, height * 0.35f) to "Deep Wicket",
                Offset(width * 0.85f, height * 0.68f) to "Deep Square",
                Offset(width * 0.40f, height * 0.40f) to "Mid-off",
                Offset(width * 0.60f, height * 0.40f) to "Mid-on",
                Offset(width * 0.50f, height * 0.30f) to "Bowler",
                Offset(width * 0.50f, height * 0.70f) to "Keeper",
                Offset(width * 0.35f, height * 0.62f) to "Point"
            )
            else -> listOf(
                Offset(width * 0.58f, height * 0.65f) to "Slip",
                Offset(width * 0.30f, height * 0.60f) to "Point",
                Offset(width * 0.28f, height * 0.42f) to "Cover",
                Offset(width * 0.40f, height * 0.32f) to "Mid-off",
                Offset(width * 0.60f, height * 0.32f) to "Mid-on",
                Offset(width * 0.72f, height * 0.44f) to "Mid-wicket",
                Offset(width * 0.78f, height * 0.65f) to "Square Leg",
                Offset(width * 0.86f, height * 0.76f) to "Fine Leg",
                Offset(width * 0.14f, height * 0.25f) to "Deep Extra",
                Offset(width * 0.50f, height * 0.30f) to "Bowler",
                Offset(width * 0.50f, height * 0.70f) to "Keeper"
            )
        }

        positions.forEachIndexed { i, (pos, label) ->
            // Player dot
            drawCircle(
                color = CricketGold,
                center = pos,
                radius = 7.5f
            )
            drawCircle(
                color = CricketDarkNavy,
                center = pos,
                radius = 5.5f
            )

            // Label
            val measured = textMeasurer.measure(
                text = label,
                style = TextStyle(color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(pos.x - (measured.size.width / 2), pos.y + 8f)
            )
        }
    }
}
