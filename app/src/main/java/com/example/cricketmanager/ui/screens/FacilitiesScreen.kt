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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.FacilityStatus
import com.example.cricketmanager.data.model.FacilityType
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun FacilitiesScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val facilities by viewModel.facilities.collectAsState()
    val userPurse by viewModel.userPurseLakhs.collectAsState()

    val purseFormatted = if (userPurse >= 100) "₹${String.format("%.2f", userPurse / 100.0)} Cr" else "₹${userPurse} L"

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
                        text = "CLUB INFRASTRUCTURE",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Purse: $purseFormatted • Stadium & Complexes",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            PremiumCard(borderColor = CricketGold, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FRANCHISE MASTER PLAN",
                    color = CricketGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Investing in infrastructure yields long-term competitive advantages: higher ticket sales, accelerated player growth, rapid medical rehab, and superior wonderkid generation.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            SectionHeader(title = "Facility Complexes", subtitle = "Tap to commission structural upgrades")
        }

        items(facilities) { facility ->
            FacilityRow(
                facility = facility,
                userPurse = userPurse,
                onUpgrade = { viewModel.upgradeFacility(facility.type) }
            )
        }
    }
}

@Composable
fun FacilityRow(
    facility: FacilityStatus,
    userPurse: Int,
    onUpgrade: () -> Unit
) {
    val canAfford = userPurse >= facility.upgradeCostLakhs && facility.level < facility.maxLevel

    val icon: ImageVector = when (facility.type) {
        FacilityType.STADIUM -> Icons.Default.Stadium
        FacilityType.TRAINING_COMPLEX -> Icons.Default.FitnessCenter
        FacilityType.MEDICAL_CENTRE -> Icons.Default.LocalHospital
        FacilityType.YOUTH_ACADEMY -> Icons.Default.School
        FacilityType.COMMERCIAL_CENTRE -> Icons.Default.Business
    }

    PremiumCard(
        borderColor = if (facility.level >= 3) CricketGold else Color(0x33FFD700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CricketNavySurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = CricketGold, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = facility.type.displayName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = facility.type.description, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavySurfaceElevated)
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LVL ${facility.level}/${facility.maxLevel}",
                    color = CricketGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar for facility tier
        StatBar(
            label = "Infrastructure Tier",
            value = facility.level,
            maxValue = facility.maxLevel,
            fillBrush = GoldGradient
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (facility.level < facility.maxLevel) {
            GoldButton(
                text = "UPGRADE TO LVL ${facility.level + 1} (${facility.upgradeCostFormatted})",
                onClick = onUpgrade,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
                height = 38.dp,
                icon = Icons.Default.Upgrade
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF143020))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "WORLD-CLASS FACILITY (MAX TIER REACHED)",
                    color = CricketStadiumGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
