package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity
import kotlin.math.abs

/**
 * Lightweight manager-AI valuation model used by auctions and squad planning.
 * Pure Kotlin: no Android, Compose, Room, or ViewModel dependencies.
 */
enum class ManagerPersonality {
    AGGRESSIVE,
    YOUTH_FOCUSED,
    BUDGET_CONSCIOUS,
    SPECIALIST_HEAVY,
    BALANCED
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
    val utilityScore: Double
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
        val ageFactor = when {
            player.matchesPlayed == 0 -> 1.05
            else -> 1.0
        }
        val baseSkill = (player.battingSkill * 0.52 + player.bowlingSkill * 0.33 + player.fieldingSkill * 0.15)
        val roleSpecialism = when (personality) {
            ManagerPersonality.SPECIALIST_HEAVY ->
                if (player.role.name in setOf("ALL_ROUNDER", "BOWLER", "WICKET_KEEPER")) 1.14 else 0.93
            else -> 1.0
        }
        val youth = when {
            player.matchesPlayed == 0 -> 1.12
            else -> 1.0
        }
        val personalityFactor = when (personality) {
            ManagerPersonality.AGGRESSIVE -> 1.18
            ManagerPersonality.YOUTH_FOCUSED -> youth * 1.15
            ManagerPersonality.BUDGET_CONSCIOUS -> 0.82
            ManagerPersonality.SPECIALIST_HEAVY -> roleSpecialism
            ManagerPersonality.BALANCED -> 1.0
        }
        val utility = (baseSkill * roleNeed * ageFactor * personalityFactor).coerceIn(20.0, 150.0)
        val fair = (utility * 7.5).toInt().coerceIn(30, 2500)
        val squadPressure = if (currentSquadSize >= maxSquadSize - 3) 0.78 else 1.0
        val purseLimit = (purseLakhs * 0.35).toInt().coerceAtLeast(30)
        val maxBid = minOf((fair * personalityFactor * roleNeed * squadPressure).toInt(), purseLimit)

        return PlayerValuation(
            fairValueLakhs = fair,
            maximumBidLakhs = maxOf(30, maxBid),
            utilityScore = utility
        )
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
