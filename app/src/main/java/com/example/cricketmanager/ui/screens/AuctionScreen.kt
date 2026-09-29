package com.example.cricketmanager.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.R
import com.example.cricketmanager.ui.components.*
import com.example.cricketmanager.ui.theme.*
import com.example.cricketmanager.viewmodel.CricketViewModel
import kotlinx.coroutines.delay

@Composable
fun AuctionScreen(
    viewModel: CricketViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAuctionActive by viewModel.isSeasonAuctionActive.collectAsState()
    val seasonNumber by viewModel.seasonNumber.collectAsState()
    val userPurse by viewModel.userPurseLakhs.collectAsState()
    val pool by viewModel.auctionPlayersPool.collectAsState()
    val currentIndex by viewModel.currentAuctionIndex.collectAsState()
    val timerSeconds by viewModel.auctionTimer.collectAsState()

    // Formatted purse
    val purseFormatted = if (userPurse >= 100) "₹${String.format("%.2f", userPurse / 100.0)} Cr" else "₹${userPurse} Lakhs"

    if (!isAuctionActive) {
        // Locked / Pre-Season State (matching requirement: "make auction not available as an option directly it will show up when season starts everytime")
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CricketNavyBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF423400), Color(0xFF1B1400))))
                        .border(BorderStroke(2.dp, CricketGold), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = CricketGold,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MEGA AUCTION HALL",
                    color = CricketGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Player Auctions unlock automatically at the start of every season. Prepare your franchise budget, scout top targets, and bid against rival teams when the season begins.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                PremiumCard(
                    borderColor = CricketGoldBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "CURRENT SEASON", color = TextMuted, fontSize = 12.sp)
                        Text(text = "Season $seasonNumber", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "FRANCHISE PURSE", color = TextMuted, fontSize = 12.sp)
                        Text(text = purseFormatted, color = CricketGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                GoldButton(
                    text = "START SEASON $seasonNumber MEGA AUCTION",
                    onClick = { viewModel.triggerSeasonStartAuction() },
                    modifier = Modifier.fillMaxWidth(),
                    height = 52.dp,
                    icon = Icons.Default.FlashOn
                )

                Spacer(modifier = Modifier.height(12.dp))

                PremiumButton(
                    text = "BACK TO DASHBOARD",
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth(),
                    height = 46.dp
                )
            }
        }
    } else {
        // LIVE CINEMATIC AUCTION ROOM
        val currentItem = pool.getOrNull(currentIndex)

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CricketNavyBackground)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Cinematic Header with Auction Hall Image
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_auction_hall_1790683787918),
                            contentDescription = "Auction Hall",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xAA090D1C), CricketNavyBackground)
                                    )
                                )
                        )

                        // Top navigation row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x88000000))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xCC090D1C))
                                    .border(1.dp, CricketGold, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = CricketGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PURSE: $purseFormatted",
                                        color = CricketGoldLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                if (currentItem != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Player on stage card
                            AuctionCard(
                                playerName = currentItem.name,
                                role = currentItem.role.name.replace("_", " "),
                                rating = currentItem.rating,
                                basePrice = currentItem.basePriceFormatted,
                                currentBid = currentItem.currentBidFormatted,
                                highestBidder = currentItem.highestBidderTeam,
                                remainingPurse = purseFormatted,
                                onBidClick = { viewModel.placeUserBid() },
                                onPassClick = { viewModel.passAuctionItem() }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Auction Stage Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LOT ${currentIndex + 1} OF ${pool.size}",
                                    color = CricketGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Player nationality: ${currentItem.nationality} • Age ${currentItem.age}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "AUCTION CONCLUDED!",
                                color = CricketGold,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All lots have been hammered. Check your squad to view newly signed superstars!",
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            GoldButton(
                                text = "RETURN TO SQUAD",
                                onClick = {
                                    viewModel.completeAuction()
                                    onNavigateBack()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Previous Lots History
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Auction Summary",
                        subtitle = "Hammered Lots",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(pool.filter { it.isSold || it.isPassed }) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CricketNavySurfaceElevated)
                            .border(0.5.dp, Color(0x33FFD700), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = item.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (item.isSold) "SOLD to ${item.highestBidderTeam}" else "UNSOLD",
                                    color = if (item.isSold) CricketStadiumGreen else CricketRedAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = item.currentBidFormatted,
                                color = CricketGoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
