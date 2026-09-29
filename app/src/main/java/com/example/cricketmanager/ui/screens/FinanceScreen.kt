package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.background
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
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun FinanceScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userPurse by viewModel.userPurseLakhs.collectAsState()
    val totalPurse = 10000 // ₹100.00 Cr
    val remainingPurse = userPurse
    val spentPurse = maxOf(0, totalPurse - remainingPurse)

    val totalPurseStr = "₹100.00 Cr"
    val remainingPurseStr = "₹${String.format("%.2f", remainingPurse / 100.0)} Cr"
    val spentPurseStr = "₹${String.format("%.2f", spentPurse / 100.0)} Cr"

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
                        text = "FRANCHISE FINANCES",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Purse, Revenues & Budget Allocations",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            FinanceCard(
                totalPurse = totalPurseStr,
                remainingPurse = remainingPurseStr,
                spentPurse = spentPurseStr,
                playerSalaries = "₹45.00 Cr",
                youthBudget = "₹12.00 Cr",
                stadiumUpgrades = "₹8.50 Cr"
            )
        }

        // Financial Operations Summary
        item {
            SectionHeader(
                title = "Revenue Streams",
                subtitle = "Active franchise contracts & income"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RevenueRow("Broadcast Rights Distribution", "+₹35.00 Cr / yr", CricketStadiumGreen, Icons.Default.LiveTv)
                RevenueRow("Title Jersey Sponsorship", "+₹24.00 Cr / yr", CricketGold, Icons.Default.Business)
                RevenueRow("Ticket Sales & Hospitality", "+₹15.50 Cr / yr", CricketStadiumBlue, Icons.Default.ConfirmationNumber)
                RevenueRow("Merchandise & Fan Token Sales", "+₹6.20 Cr / yr", CricketStadiumPurple, Icons.Default.ShoppingBag)
            }
        }

        item {
            SectionHeader(
                title = "Operating Expenditure",
                subtitle = "Payroll & capital investments"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RevenueRow("Playing Squad Wages", "-₹42.00 Cr", CricketRedAccent, Icons.Default.Group)
                RevenueRow("Youth Academy Operations", "-₹6.00 Cr", CricketRedAccent, Icons.Default.School)
                RevenueRow("Support Staff & Scouting Network", "-₹4.50 Cr", CricketRedAccent, Icons.Default.Engineering)
                RevenueRow("Stadium Maintenance & Training Facility", "-₹3.20 Cr", CricketRedAccent, Icons.Default.Stadium)
            }
        }
    }
}

@Composable
fun RevenueRow(
    title: String,
    amount: String,
    amountColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketNavySurfaceElevated)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Text(text = amount, color = amountColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
