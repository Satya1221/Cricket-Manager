package com.example.cricketmanager.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.PlayerRole
import com.example.cricketmanager.ui.theme.*

@Composable
fun PlayerCard(
    player: PlayerEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val overallRating = ((player.battingSkill + player.bowlingSkill + player.fieldingSkill) / 3)

    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Avatar Portrait Placeholder with Jersey
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF23305E), Color(0xFF131A36))
                        )
                    )
                    .border(BorderStroke(1.dp, CricketGoldBorder), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = null,
                        tint = CricketGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "#${player.jerseyNumber}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (player.isCaptain) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketGold)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "C",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    if (player.isWicketKeeper) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CricketStadiumBlue)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "WK",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.role.name.replace("_", " "),
                        color = CricketGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "BAT: ${player.battingSkill}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " | ",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "BWL: ${player.bowlingSkill}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            RatingBadge(rating = overallRating, size = 40.dp)
        }
    }
}

@Composable
fun PlayerRow(
    player: PlayerEntity,
    modifier: Modifier = Modifier,
    trailingAction: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val overallRating = ((player.battingSkill + player.bowlingSkill + player.fieldingSkill) / 3)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CricketNavySurfaceElevated)
            .border(BorderStroke(0.5.dp, Color(0x33FFD700)), RoundedCornerShape(10.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Order or Jersey badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CricketNavySurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${player.battingOrder}",
                color = CricketGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = player.name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (player.isCaptain) {
                    Text(
                        text = " (C)",
                        color = CricketGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (player.isWicketKeeper) {
                    Text(
                        text = " (WK)",
                        color = CricketStadiumNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "${player.role.name.replace("_", " ")} • Bat: ${player.battingSkill} Bowl: ${player.bowlingSkill}",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        RatingBadge(rating = overallRating, size = 32.dp)

        if (trailingAction != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingAction()
        }
    }
}

@Composable
fun MatchCard(
    team1Name: String,
    team1ShortCode: String,
    team1ColorHex: String,
    team2Name: String,
    team2ShortCode: String,
    team2ColorHex: String,
    venue: String,
    statusText: String,
    scoreText: String? = null,
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
    actionButtonText: String = "MATCH CENTRE",
    onActionClick: () -> Unit
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (isLive) CricketGold else Color(0x33FFD700),
        borderWidth = if (isLive) 1.5.dp else 1.dp
    ) {
        // Status row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CricketRedAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE NOW",
                        color = CricketRedAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                Text(
                    text = statusText.uppercase(),
                    color = CricketGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = venue,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Teams VS display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team 1
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamBadge(
                    shortCode = team1ShortCode,
                    primaryColorHex = team1ColorHex,
                    size = 46.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = team1Name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = team1ShortCode,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // VS Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CricketNavySurfaceElevated)
                    .border(BorderStroke(1.dp, CricketGoldBorder), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    color = CricketGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Team 2
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = team2Name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = team2ShortCode,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                TeamBadge(
                    shortCode = team2ShortCode,
                    primaryColorHex = team2ColorHex,
                    size = 46.dp
                )
            }
        }

        if (scoreText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CricketNavySurface)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = scoreText,
                    color = CricketGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLive) {
            GoldButton(
                text = actionButtonText,
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(),
                height = 42.dp,
                icon = Icons.Default.SportsCricket
            )
        } else {
            PremiumButton(
                text = actionButtonText,
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(),
                height = 42.dp,
                icon = Icons.Default.PlayArrow
            )
        }
    }
}

@Composable
fun AuctionCard(
    playerName: String,
    role: String,
    rating: Int,
    basePrice: String,
    currentBid: String,
    highestBidder: String,
    remainingPurse: String,
    onBidClick: () -> Unit,
    onPassClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUserTurn: Boolean = true
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = CricketGold,
        borderWidth = 1.5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF533F00), Color(0xFF281E00))))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "AUCTION STAGE",
                    color = CricketGoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = "YOUR PURSE: $remainingPurse",
                color = CricketStadiumNeon,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF242E54))
                    .border(BorderStroke(1.5.dp, CricketGold), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CricketGold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playerName,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$role • Base: $basePrice",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            RatingBadge(rating = rating, size = 44.dp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Current Bid Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CricketNavySurfaceElevated)
                .border(BorderStroke(1.dp, Color(0x33FFD700)), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CURRENT BID",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentBid,
                        color = CricketGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "HIGHEST BIDDER",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = highestBidder,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumButton(
                text = "PASS",
                onClick = onPassClick,
                modifier = Modifier.weight(1f),
                textColor = TextSecondary,
                borderColor = Color(0xFF434E70),
                containerColor = CricketNavySurfaceElevated
            )

            GoldButton(
                text = "RAISE BID",
                onClick = onBidClick,
                enabled = isUserTurn,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Gavel
            )
        }
    }
}

@Composable
fun NewsCard(
    headline: String,
    category: String,
    date: String,
    snippet: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CricketNavySurfaceElevated)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = category.uppercase(),
                    color = CricketGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = date,
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = headline,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = snippet,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TrophyCard(
    title: String,
    competition: String,
    seasonWon: String?,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (isUnlocked) CricketGold else Color(0xFF2E3758),
        borderWidth = if (isUnlocked) 1.5.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Brush.radialGradient(listOf(Color(0xFF5E4900), Color(0xFF281E00)))
                        else Brush.radialGradient(listOf(Color(0xFF232A42), Color(0xFF131726)))
                    )
                    .border(
                        BorderStroke(1.dp, if (isUnlocked) CricketGold else Color(0xFF455075)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) CricketGold else TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isUnlocked) TextPrimary else TextMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = competition,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                if (isUnlocked && seasonWon != null) {
                    Text(
                        text = "Champion: $seasonWon",
                        color = CricketGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Locked - Win tournament to claim",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceCard(
    totalPurse: String,
    remainingPurse: String,
    spentPurse: String,
    playerSalaries: String,
    youthBudget: String,
    stadiumUpgrades: String,
    modifier: Modifier = Modifier
) {
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = CricketGoldBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "REMAINING PURSE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = remainingPurse,
                    color = CricketGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "TOTAL PURSE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = totalPurse,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Breakdown bars
        Text(
            text = "BUDGET ALLOCATION",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        StatBar(label = "Player Salaries", value = 65, maxValue = 100, fillBrush = GoldGradient)
        Spacer(modifier = Modifier.height(6.dp))
        StatBar(
            label = "Youth Academy",
            value = 20,
            maxValue = 100,
            fillBrush = Brush.horizontalGradient(listOf(CricketStadiumBlue, CricketStadiumNeon))
        )
        Spacer(modifier = Modifier.height(6.dp))
        StatBar(
            label = "Stadium & Facilities",
            value = 15,
            maxValue = 100,
            fillBrush = Brush.horizontalGradient(listOf(CricketStadiumPurple, Color(0xFFBA68C8)))
        )
    }
}

@Composable
fun PremiumBottomNavigation(
    selectedRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    data class NavItem(val route: String, val title: String, val icon: ImageVector)

    val items = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("squad", "Squad", Icons.Default.Group),
        NavItem("matches", "Matches", Icons.Default.SportsCricket),
        NavItem("youth", "Youth", Icons.Default.School),
        NavItem("more", "More", Icons.Default.Widgets)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CricketNavyBackground,
        border = BorderStroke(1.dp, Color(0x33FFD700)),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute == item.route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(item.route) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_${item.route}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 36.dp else 30.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) Color(0xFF281E02) else Color.Transparent
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        BorderStroke(1.dp, CricketGold),
                                        RoundedCornerShape(10.dp)
                                    ) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) CricketGold else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            color = if (isSelected) CricketGold else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
