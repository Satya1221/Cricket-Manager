package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity
import kotlin.math.roundToInt

data class PlayerDevelopmentResult(
    val playerId: Long,
    val battingSkillAfter: Int,
    val bowlingSkillAfter: Int,
    val fieldingSkillAfter: Int,
    val performanceScore: Int
)

object PlayerDevelopmentEngine {
    /**
     * Development now reacts to age, potential, form and actual career output.
     * A player who performs consistently gets more training value, while a player
     * close to their potential naturally slows down.
     */
    fun develop(
        player: PlayerEntity,
        trainingFocus: TrainingFocus,
        intensity: TrainingIntensity
    ): PlayerDevelopmentResult {
        val performanceScore = calculatePerformanceScore(player)

        val youthBase = when {
            player.age <= 21 -> 3
            player.age <= 24 -> 2
            player.age <= 28 -> 1
            else -> 0
        }
        val experienceBase = when {
            player.matchesPlayed < 20 -> 2
            player.matchesPlayed < 60 -> 1
            else -> 0
        }
        val intensityBoost = when (intensity) {
            TrainingIntensity.LIGHT -> 0
            TrainingIntensity.STANDARD -> 1
            TrainingIntensity.HARD -> 2
            TrainingIntensity.EXTREME -> 3
        }

        val performanceBoost = when {
            performanceScore >= 90 -> 3
            performanceScore >= 80 -> 2
            performanceScore >= 65 -> 1
            performanceScore < 40 -> 0
            else -> 1
        }

        val potentialGap = player.developmentGap
        val potentialMultiplier = when {
            potentialGap >= 20 -> 1.20
            potentialGap >= 10 -> 1.10
            potentialGap >= 5 -> 1.0
            else -> 0.60
        }

        val rawBoost = youthBase + experienceBase + intensityBoost + performanceBoost
        val boost = (rawBoost * potentialMultiplier).roundToInt().coerceIn(0, 7)

        val battingDelta = when (trainingFocus) {
            TrainingFocus.BATTING -> boost
            TrainingFocus.BALANCED -> (boost * 0.60).roundToInt()
            else -> 0
        }
        val bowlingDelta = when (trainingFocus) {
            TrainingFocus.BOWLING -> boost
            TrainingFocus.BALANCED -> (boost * 0.60).roundToInt()
            else -> 0
        }
        val fieldingDelta = when (trainingFocus) {
            TrainingFocus.FIELDING, TrainingFocus.FITNESS -> boost
            TrainingFocus.BALANCED -> (boost * 0.60).roundToInt()
            else -> 0
        }

        return PlayerDevelopmentResult(
            playerId = player.id,
            battingSkillAfter = (player.battingSkill + battingDelta).coerceIn(1, 100),
            bowlingSkillAfter = (player.bowlingSkill + bowlingDelta).coerceIn(1, 100),
            fieldingSkillAfter = (player.fieldingSkill + fieldingDelta).coerceIn(1, 100),
            performanceScore = performanceScore
        )
    }

    /**
     * Converts accumulated match output into a stable 0-100 development signal.
     * This is deliberately role-aware so bowlers are not judged like batsmen.
     */
    fun calculatePerformanceScore(player: PlayerEntity): Int {
        val battingSignal = when {
            player.ballsFaced <= 0 -> 50
            else -> ((player.strikeRate / 2.0) + (player.battingAverage * 0.45)).coerceIn(0.0, 100.0)
        }
        val bowlingSignal = when {
            player.matchesPlayed <= 0 -> 50.0
            else -> {
                val wicketsRate = (player.wicketsTaken.toDouble() / player.matchesPlayed) * 22.0
                val economyBonus = if (player.bowlingEconomy in 0.0..7.0) 25.0 else 12.0
                (wicketsRate + economyBonus).coerceIn(0.0, 100.0)
            }
        }

        val roleScore = when (player.role.name) {
            "BOWLER" -> bowlingSignal * 0.70 + player.fieldingSkill * 0.10 + player.form * 0.20
            "ALL_ROUNDER" -> battingSignal * 0.35 + bowlingSignal * 0.40 + player.fieldingSkill * 0.10 + player.form * 0.15
            "WICKET_KEEPER" -> battingSignal * 0.55 + player.fieldingSkill * 0.20 + player.form * 0.25
            else -> battingSignal * 0.65 + player.fieldingSkill * 0.10 + player.form * 0.25
        }

        return roleScore.roundToInt().coerceIn(0, 100)
    }
}

enum class TrainingFocus { BATTING, BOWLING, FIELDING, FITNESS, BALANCED }
enum class TrainingIntensity { LIGHT, STANDARD, HARD, EXTREME }
