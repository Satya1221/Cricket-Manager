package com.example.cricketmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.R
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel

@Composable
fun HomeScreen(
    viewModel: CricketViewModel,
    onNavigateToMatch: (Long) -> Unit,
    onNavigateToMatchCentre: () -> Unit,
    onNavigateToSquad: () -> Unit,
    onNavigateToTactics: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToScouting: () -> Unit,
    onNavigateToYouth: () -> Unit,
    onNavigateToAuction: () -> Unit,
    onNavigateToLeague: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToTrophies: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTeams by viewModel.allTeams.collectAsState()
    val activeMatch by viewModel.activeMatch.collectAsState()
    val recentMatches by viewModel.recentMatches.collectAsState()
    val seasonNum by viewModel.seasonNumber.collectAsState()
    val userPurse by viewModel.userPurseLakhs.collectAsState()
    val isAuctionActive by viewModel.isSeasonAuctionActive.collectAsState()
    val selectedTeam by viewModel.selectedTeam.collectAsState()

    val myTeam = selectedTeam ?: allTeams.firstOrNull()
    val purseFormatted = if (userPurse >= 100) "₹${String.format("%.2f", userPurse / 100.0)} Cr" else "₹${userPurse} L"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CricketNavyBackground),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top Franchise Header
        item {
            TopBar(
                teamName = myTeam?.name ?: "Mumbai Strikers",
                purseAmount = purseFormatted,
                seasonNumber = seasonNum,
                teamBadgeCode = myTeam?.shortCode ?: "MS",
                teamColorHex = myTeam?.primaryColorHex ?: "#1E88E5",
                onNotificationClick = onNavigateToNews
            )
        }

        // Hero Stadium Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cricket_stadium_banner_1790682263928),
                    contentDescription = "Stadium",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x22000000), Color(0xBB080C1E), CricketNavyBackground)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "PREMIER CRICKET LEAGUE",
                        color = CricketGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Season $seasonNum • Championship Journey",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // SEASON MEGA AUCTION CARD
        // (Matching user requirement: "make auction not available as an option directly it will show up when season starts everytime")
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PremiumCard(
                    borderColor = if (isAuctionActive) CricketGold else Color(0x66FFD700),
                    borderWidth = if (isAuctionActive) 2.dp else 1.dp,
                    backgroundColor = if (isAuctionActive) Color(0xFF231B00) else CricketNavyCard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3E3100))
                                    .border(1.dp, CricketGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = CricketGold, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAuctionActive) "SEASON MEGA AUCTION IS LIVE!" else "SEASON $seasonNum PLAYER AUCTIONS",
                                    color = CricketGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isAuctionActive) "Franchises bidding now • Enter auction hall" else "Unlocks at season start • Prepare budget ($purseFormatted)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isAuctionActive) {
                        GoldButton(
                            text = "ENTER LIVE AUCTION HALL",
                            onClick = onNavigateToAuction,
                            modifier = Modifier.fillMaxWidth(),
                            height = 40.dp,
                            icon = Icons.Default.FlashOn
                        )
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PremiumButton(
                                text = "VIEW AUCTION POOL",
                                onClick = onNavigateToAuction,
                                modifier = Modifier.weight(1f),
                                height = 38.dp,
                                icon = Icons.Default.ListAlt
                            )
                            GoldButton(
                                text = "START SEASON AUCTION",
                                onClick = {
                                    viewModel.triggerSeasonStartAuction()
                                    onNavigateToAuction()
                                },
                                modifier = Modifier.weight(1f),
                                height = 38.dp,
                                icon = Icons.Default.PlayArrow
                            )
                        }
                    }
                }
            }
        }

        // NEXT / LIVE MATCH CARD
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (activeMatch != null) {
                    val m = activeMatch!!
                    val t1 = allTeams.find { it.id == m.team1Id }
                    val t2 = allTeams.find { it.id == m.team2Id }
                    MatchCard(
                        team1Name = t1?.name ?: "Team 1",
                        team1ShortCode = t1?.shortCode ?: "T1",
                        team1ColorHex = t1?.primaryColorHex ?: "#1E88E5",
                        team2Name = t2?.name ?: "Team 2",
                        team2ShortCode = t2?.shortCode ?: "T2",
                        team2ColorHex = t2?.primaryColorHex ?: "#FFC107",
                        venue = t1?.homeGround ?: "National Stadium",
                        statusText = "LIVE NOW",
                        scoreText = "${m.currentRuns}/${m.currentWickets} (${m.currentOversFormatted} ov) • CRR ${String.format("%.2f", m.currentRunRate)}",
                        isLive = true,
                        actionButtonText = "RESUME 3D MATCH",
                        onActionClick = { onNavigateToMatch(m.id) }
                    )
                } else {
                    val oppTeam = allTeams.filter { it.id != myTeam?.id }.randomOrNull() ?: allTeams.getOrNull(1)
                    MatchCard(
                        team1Name = myTeam?.name ?: "Mumbai Strikers",
                        team1ShortCode = myTeam?.shortCode ?: "MS",
                        team1ColorHex = myTeam?.primaryColorHex ?: "#1E88E5",
                        team2Name = oppTeam?.name ?: "Chennai Super Kings",
                        team2ShortCode = oppTeam?.shortCode ?: "CSK",
                        team2ColorHex = oppTeam?.primaryColorHex ?: "#FFC107",
                        venue = myTeam?.homeGround ?: "National Arena",
                        statusText = "UPCOMING FIXTURE",
                        scoreText = "Toss & Pitch Analysis Available",
                        isLive = false,
                        actionButtonText = "OPEN MATCH CENTRE",
                        onActionClick = onNavigateToMatchCentre
                    )
                }
            }
        }

        // YOUTH ACADEMY SPOTLIGHT CARD
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                PremiumCard(
                    borderColor = CricketStadiumNeon,
                    borderWidth = 1.dp,
                    onClick = onNavigateToYouth
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF062B3D))
                                    .border(1.dp, CricketStadiumNeon, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.School, contentDescription = null, tint = CricketStadiumNeon, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "YOUTH ACADEMY", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Scout, train & promote promising wonderkids", color = CricketStadiumNeon, fontSize = 11.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                    }
                }
            }
        }

        // QUICK HUB NAVIGATION TILES
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "Manager Operations", subtitle = "Franchise command center")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HubTile("Squad", Icons.Default.Group, CricketGold, Modifier.weight(1f), onNavigateToSquad)
                        HubTile("Tactics 3D", Icons.Default.SportsCricket, CricketStadiumBlue, Modifier.weight(1f), onNavigateToTactics)
                        HubTile("Training", Icons.Default.FitnessCenter, CricketStadiumGreen, Modifier.weight(1f), onNavigateToTraining)
                        HubTile("Scouting", Icons.Default.Search, CricketStadiumPurple, Modifier.weight(1f), onNavigateToScouting)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HubTile("League", Icons.Default.Leaderboard, CricketGold, Modifier.weight(1f), onNavigateToLeague)
                        HubTile("Finance", Icons.Default.MonetizationOn, CricketStadiumGreen, Modifier.weight(1f), onNavigateToFinance)
                        HubTile("Trophies", Icons.Default.EmojiEvents, CricketGold, Modifier.weight(1f), onNavigateToTrophies)
                        HubTile("Settings", Icons.Default.Settings, TextSecondary, Modifier.weight(1f), onNavigateToSettings)
                    }
                }
            }
        }

        // LAST MATCH RESULT
        item {
            val lastMatch = recentMatches.firstOrNull { it.status == MatchStatus.COMPLETED }
            if (lastMatch != null) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(title = "Last Match Result", subtitle = "Completed fixture")
                    PremiumCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = lastMatch.resultDescription, color = CricketGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inn 1: ${lastMatch.inn1Runs}/${lastMatch.inn1Wickets} • Inn 2: ${lastMatch.inn2Runs}/${lastMatch.inn2Wickets}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HubTile(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CricketNavySurfaceElevated)
            .border(0.5.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
