package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity

/** Result of one AI auction decision. */
data class AiBidResult(
    val franchiseTeamId: Long,
    val bidLakhs: Int
)

data class AiFranchise(
    val teamId: Long,
    val purseLakhs: Int,
    val personality: ManagerPersonality,
    val needs: FranchiseNeeds
)

object AuctionEngine {
    private const val INCREMENT = 25

    fun processAiBids(
        currentPlayer: PlayerEntity,
        currentBidLakhs: Int,
        franchises: List<AiFranchise>
    ): AiBidResult? {
        return franchises
            .mapNotNull { franchise ->
                val valuation = ManagerAiEngine.evaluatePlayer(
                    player = currentPlayer,
                    personality = franchise.personality,
                    needs = franchise.needs,
                    purseLakhs = franchise.purseLakhs,
                    currentSquadSize = 0
                )
                val nextBid = currentBidLakhs + INCREMENT
                if (valuation.maximumBidLakhs >= nextBid && franchise.purseLakhs >= nextBid) {
                    franchise to nextBid
                } else null
            }
            .maxByOrNull { it.second }
            ?.let { (franchise, bid) -> AiBidResult(franchise.teamId, bid) }
    }

    fun nextBid(currentBidLakhs: Int): Int =
        currentBidLakhs + when {
            currentBidLakhs < 100 -> 10
            currentBidLakhs < 300 -> 25
            currentBidLakhs < 1000 -> 50
            else -> 100
        }
}
