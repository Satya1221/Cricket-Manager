package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity

/** Lightweight manager-AI valuation model used by auctions and squad planning. */
enum class ManagerPersonality {
    AGGRESSIVE,
    YOUTH_FOCUSED,
    BUDGET_CONSCIOUS,
    SPECIALIST_HEAVY,
    BALANCED
}

enum class AuctionTier {
    MARQUEE,
    PREMIUM,
    CORE,
    VALUE,
    DEVELOPMENT
}

data class FranchiseNeeds(
    val batsmen: Int = 0,
    val bowlers: Int = 0,
    val allRounders: Int = 0,
    val wicketKeepers: Int = 0,
    val overseasSlots: Int = 0
)

data class PlayerValuation(
    val fairValueLakhs: Int,
    val maximumBidLakhs: Int,
    val utilityScore: Double,
    val tier: AuctionTier
)

object ManagerAiEngine {
    fun evaluatePlayer(
        player: PlayerEntity,
        personality: ManagerPersonality,
        needs: FranchiseNeeds,
        purseLakhs: Int,
        currentSquadSize: Int,
        maxSquadSize: Int = 25
    ): PlayerValuation {
        val roleNeed = when (player.role.name) {
            "BATSMAN" -> 1.0 + needs.batsmen.coerceAtMost(4) * 0.10
            "BOWLER" -> 1.0 + needs.bowlers.coerceAtMost(4) * 0.10
            "ALL_ROUNDER" -> 1.0 + needs.allRounders.coerceAtMost(4) * 0.12
            "WICKET_KEEPER" -> 1.0 + needs.wicketKeepers.coerceAtMost(2) * 0.20
            else -> 1.0
        }

        val skillBase = when (player.role.name) {
            "BOWLER" -> player.bowlingSkill * 0.65 + player.fieldingSkill * 0.15 + player.battingSkill * 0.20
            "ALL_ROUNDER" -> player.battingSkill * 0.42 + player.bowlingSkill * 0.43 + player.fieldingSkill * 0.15
            "WICKET_KEEPER" -> player.battingSkill * 0.55 + player.fieldingSkill * 0.30 + player.bowlingSkill * 0.15
            else -> player.battingSkill * 0.65 + player.fieldingSkill * 0.15 + player.bowlingSkill * 0.20
        }

        val ageFactor = when {
            player.age <= 21 -> 1.10
            player.age <= 24 -> 1.07
            player.age <= 28 -> 1.03
            player.age <= 32 -> 1.0
            player.age <= 35 -> 0.92
            else -> 0.82
        }
        val potentialFactor = (0.85 + (player.potentialSkill / 100.0) * 0.30).coerceIn(0.85, 1.15)
        val formFactor = (0.85 + (player.form / 100.0) * 0.30).coerceIn(0.85, 1.15)
        val confidenceFactor = (0.92 + (player.confidence / 100.0) * 0.16).coerceIn(0.92, 1.08)

        val youthFactor = if (player.age <= 23 || player.matchesPlayed < 20) 1.10 else 1.0
        val roleSpecialism = when (personality) {
            ManagerPersonality.SPECIALIST_HEAVY ->
                if (player.role.name in setOf("ALL_ROUNDER", "BOWLER", "WICKET_KEEPER")) 1.14 else 0.93
            else -> 1.0
        }
        val personalityFactor = when (personality) {
            ManagerPersonality.AGGRESSIVE -> 1.18
            ManagerPersonality.YOUTH_FOCUSED -> youthFactor * 1.15
            ManagerPersonality.BUDGET_CONSCIOUS -> 0.82
            ManagerPersonality.SPECIALIST_HEAVY -> roleSpecialism
            ManagerPersonality.BALANCED -> 1.0
        }

        val utility = (
            skillBase * roleNeed * ageFactor * potentialFactor * formFactor * confidenceFactor * personalityFactor
        ).coerceIn(20.0, 150.0)

        val tier = auctionTier(player)
        val tierMultiplier = when (tier) {
            AuctionTier.MARQUEE -> 1.45
            AuctionTier.PREMIUM -> 1.25
            AuctionTier.CORE -> 1.05
            AuctionTier.VALUE -> 0.90
            AuctionTier.DEVELOPMENT -> 0.75
        }
        val fair = (utility * 7.5 * tierMultiplier).toInt().coerceIn(30, 3000)
        val squadPressure = if (currentSquadSize >= maxSquadSize - 3) 0.78 else 1.0
        val purseLimit = (purseLakhs * 0.35).toInt().coerceAtLeast(30)
        val maxBid = minOf(
            (fair * personalityFactor * roleNeed * squadPressure).toInt(),
            purseLimit
        )

        return PlayerValuation(
            fairValueLakhs = fair,
            maximumBidLakhs = maxOf(30, maxBid),
            utilityScore = utility,
            tier = tier
        )
    }

    /**
     * Auction tiers are driven by the same attributes used for valuation, preventing
     * high-rated players from being placed in low-value brackets by accident.
     */
    fun auctionTier(player: PlayerEntity): AuctionTier {
        val rating = player.overallRating
        val potential = player.potentialSkill
        val form = player.form
        return when {
            rating >= 90 || (rating >= 87 && potential >= 92 && form >= 80) -> AuctionTier.MARQUEE
            rating >= 84 || (rating >= 80 && potential >= 90) -> AuctionTier.PREMIUM
            rating >= 75 -> AuctionTier.CORE
            rating >= 65 || potential >= 78 -> AuctionTier.VALUE
            else -> AuctionTier.DEVELOPMENT
        }
    }

    fun shouldBid(currentBidLakhs: Int, valuation: PlayerValuation): Boolean =
        currentBidLakhs < valuation.maximumBidLakhs

    fun bidIncrementLakhs(currentBidLakhs: Int): Int = when {
        currentBidLakhs < 100 -> 10
        currentBidLakhs < 300 -> 25
        currentBidLakhs < 1000 -> 50
        else -> 100
    }
}
