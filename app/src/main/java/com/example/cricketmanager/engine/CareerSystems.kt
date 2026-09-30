package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity
import kotlin.math.roundToInt

enum class PlayerPersonality {
    LEADER,
    PROFESSIONAL,
    AGGRESSIVE,
    CALM,
    TEAM_FIRST,
    AMBITIOUS,
    YOUTHFUL
}

enum class ContractType { ROOKIE, STANDARD, STAR, MARQUEE, SHORT_TERM }

data class PlayerContract(
    val playerId: Long,
    val teamId: Long,
    val type: ContractType,
    val annualSalaryLakhs: Int,
    val yearsRemaining: Int,
    val releaseClauseLakhs: Int = 0,
    val performanceBonusLakhs: Int = 0
)

data class TransferOffer(
    val playerId: Long,
    val fromTeamId: Long,
    val toTeamId: Long,
    val feeLakhs: Int,
    val salaryLakhs: Int,
    val contractYears: Int,
    val rolePromise: String
)

enum class PressConferenceTopic {
    TEAM_FORM,
    PLAYER_FORM,
    SELECTION,
    TRANSFER_RUMOUR,
    YOUTH_DEBUT,
    BOARD_PRESSURE
}

data class PressResponse(
    val topic: PressConferenceTopic,
    val headline: String,
    val moraleDelta: Int,
    val boardConfidenceDelta: Int,
    val fanConfidenceDelta: Int
)

data class BoardExpectation(
    val id: String,
    val season: Int,
    val objective: String,
    val target: Int,
    val progress: Int = 0,
    val rewardLakhs: Int = 0,
    val confidenceWeight: Int = 10
) {
    val completionPercent: Int
        get() = if (target <= 0) 100 else ((progress.toDouble() / target) * 100).roundToInt().coerceIn(0, 100)
}

data class CareerRecord(
    val playerId: Long,
    val seasons: Int = 0,
    val matches: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val trophies: Int = 0,
    val playerOfMatchAwards: Int = 0
)

object CareerProgressionEngine {
    fun personalityEffect(personality: PlayerPersonality): Double = when (personality) {
        PlayerPersonality.LEADER -> 1.08
        PlayerPersonality.PROFESSIONAL -> 1.06
        PlayerPersonality.AGGRESSIVE -> 1.04
        PlayerPersonality.CALM -> 1.05
        PlayerPersonality.TEAM_FIRST -> 1.07
        PlayerPersonality.AMBITIOUS -> 1.10
        PlayerPersonality.YOUTHFUL -> 1.12
    }

    fun expectedSalary(player: PlayerEntity, contractType: ContractType = ContractType.STANDARD): Int {
        val ratingValue = player.overallRating * 18
        val formValue = player.form * 5
        val potentialValue = player.potentialSkill * 3
        val typeMultiplier = when (contractType) {
            ContractType.ROOKIE -> 0.55
            ContractType.STANDARD -> 1.0
            ContractType.STAR -> 1.35
            ContractType.MARQUEE -> 1.70
            ContractType.SHORT_TERM -> 1.15
        }
        return ((ratingValue + formValue + potentialValue) * typeMultiplier)
            .roundToInt()
            .coerceAtLeast(25)
    }

    fun transferValue(player: PlayerEntity): Int {
        val ageFactor = when {
            player.age <= 23 -> 1.30
            player.age <= 27 -> 1.15
            player.age <= 31 -> 1.0
            player.age <= 34 -> 0.82
            else -> 0.62
        }
        val performance = (player.overallRating * 0.55) + (player.form * 0.25) + (player.potentialSkill * 0.20)
        return (performance * performance * ageFactor * 1.8).roundToInt().coerceAtLeast(25)
    }

    fun pressConference(topic: PressConferenceTopic, positive: Boolean): PressResponse {
        return if (positive) {
            PressResponse(topic, "Manager backs the squad and focuses on the next match.", 2, 1, 2)
        } else {
            PressResponse(topic, "Manager accepts the criticism and promises a response on the field.", -1, 0, 1)
        }
    }
}
