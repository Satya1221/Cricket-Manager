package com.example.cricketmanager.data.model

data class YouthPlayer(
    val id: String,
    val name: String,
    val age: Int,
    val role: PlayerRole,
    val currentSkill: Int,
    val potentialSkill: Int,
    val battingSkill: Int,
    val bowlingSkill: Int,
    val specialty: String,
    val progress: Float = 0.35f,
    val isPromoted: Boolean = false
)

data class AuctionItem(
    val id: String,
    val name: String,
    val role: PlayerRole,
    val rating: Int,
    val age: Int,
    val nationality: String,
    val basePriceLakhs: Int,
    var currentBidLakhs: Int,
    var highestBidderTeam: String = "None",
    var highestBidderId: Long? = null,
    var isSold: Boolean = false,
    var isPassed: Boolean = false
) {
    val basePriceFormatted: String
        get() = if (basePriceLakhs >= 100) "₹${basePriceLakhs / 100.0} Cr" else "₹${basePriceLakhs} L"

    val currentBidFormatted: String
        get() = if (currentBidLakhs >= 100) "₹${String.format("%.2f", currentBidLakhs / 100.0)} Cr" else "₹${currentBidLakhs} L"
}

data class NewsItem(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val content: String
)

data class TrophyItem(
    val id: String,
    val name: String,
    val competition: String,
    val seasonWon: String?,
    val isUnlocked: Boolean,
    val description: String
)
