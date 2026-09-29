package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun MoreHubScreen(
    viewModel: CricketViewModel,
    onNavigateToTactics: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToScouting: () -> Unit,
    onNavigateToAuction: () -> Unit,
    onNavigateToFacilities: () -> Unit,
    onNavigateToMedical: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToBoard: () -> Unit,
    onNavigateToLeague: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToTrophies: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSaveLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "CLUB MANAGEMENT",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Operations, Facilities & Franchise Administration",
                    color = CricketGoldLight,
                    fontSize = 12.sp
                )
            }
        }

        item {
            SectionHeader(title = "Infrastructure & Staff")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MoreMenuTile("Club Facilities", "Upgrade stadium, training, medical & commercial hubs", Icons.Default.Stadium, CricketGold, onNavigateToFacilities)
                MoreMenuTile("Sports Science & Medical", "Squad fitness tracking, fatigue & injury rehabilitation", Icons.Default.LocalHospital, CricketRedAccent, onNavigateToMedical)
                MoreMenuTile("Coaching Staff & Scouts", "Technical directors, bowling specialists & talent scouts", Icons.Default.Badge, CricketStadiumNeon, onNavigateToStaff)
                MoreMenuTile("Board & Fan Sentiment", "Board of directors directives, objectives & fan loyalty", Icons.Default.Assignment, CricketGoldLight, onNavigateToBoard)
            }
        }

        item {
            SectionHeader(title = "Tactics & Squad Development")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MoreMenuTile("Tactics & 3D Field Placements", "Configure pitch formations and manager aggression", Icons.Default.SportsCricket, CricketGold, onNavigateToTactics)
                MoreMenuTile("Training Complex", "Individual drills for batting, bowling & fitness", Icons.Default.FitnessCenter, CricketStadiumGreen, onNavigateToTraining)
                MoreMenuTile("Global Scouting Network", "Identify and recruit domestic & overseas stars", Icons.Default.Search, CricketStadiumPurple, onNavigateToScouting)
                MoreMenuTile("Player Mega Auction", "Season-kickoff live bidding war-room", Icons.Default.Gavel, CricketGoldLight, onNavigateToAuction)
            }
        }

        item {
            SectionHeader(title = "Franchise Administration")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MoreMenuTile("League Table & Standings", "Current championship standings and NRR", Icons.Default.Leaderboard, CricketStadiumBlue, onNavigateToLeague)
                MoreMenuTile("Club Finances & Purse", "Revenue breakdown, salaries & budget allocations", Icons.Default.MonetizationOn, CricketStadiumGreen, onNavigateToFinance)
                MoreMenuTile("Trophy Showcase", "Silverware, MVP caps and honours", Icons.Default.EmojiEvents, CricketGold, onNavigateToTrophies)
                MoreMenuTile("Official Cricket Dispatch", "Press announcements, bulletins and reviews", Icons.Default.Campaign, CricketStadiumNeon, onNavigateToNews)
                MoreMenuTile("Save & Load Career", "Manage multiple save slots and backups", Icons.Default.Save, CricketStadiumBlue, onNavigateToSaveLoad)
                MoreMenuTile("Game Settings", "Simulation speed, difficulty and preferences", Icons.Default.Settings, TextSecondary, onNavigateToSettings)
            }
        }
    }
}

@Composable
fun MoreMenuTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    PremiumCard(
        borderColor = Color(0x33FFD700),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CricketNavySurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }

            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
