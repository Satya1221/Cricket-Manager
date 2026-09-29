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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.StaffMember
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun StaffScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val staffList by viewModel.staffMembers.collectAsState()

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
                        text = "COACHING STAFF & SCOUTS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Technical Directors & Specialists",
                        color = CricketGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Technical Leadership", subtitle = "Tactical and physical specialists")
        }

        items(staffList) { staff ->
            StaffMemberCard(staff = staff)
        }
    }
}

@Composable
fun StaffMemberCard(staff: StaffMember) {
    PremiumCard(
        borderColor = Color(0x33FFD700),
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
                    .background(CricketNavySurfaceElevated)
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = CricketGold, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = staff.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = staff.role.title, color = CricketGoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Specialty: ${staff.specialty}", color = TextSecondary, fontSize = 11.sp)
                Text(text = "Annual Retainer: ₹${staff.salaryLakhsPerYear} L/yr", color = TextMuted, fontSize = 10.sp)
            }

            RatingBadge(rating = staff.rating, size = 40.dp)
        }
    }
}
