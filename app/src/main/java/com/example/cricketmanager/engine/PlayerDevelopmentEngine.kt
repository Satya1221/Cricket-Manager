package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity
import java.time.LocalDate

data class PlayerDevelopmentResult(
    val playerId: Long,
    val battingSkillAfter: Int,
    val bowlingSkillAfter: Int,
    val fieldingSkillAfter: Int
)

object PlayerDevelopmentEngine {
    fun develop(
        player: PlayerEntity,
        trainingFocus: TrainingFocus,
        intensity: TrainingIntensity
    ): PlayerDevelopmentResult {
        val youthBoost = when {
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
        val boost = youthBoost + intensityBoost

        val battingDelta = when (trainingFocus) {
            TrainingFocus.BATTING -> boost
            TrainingFocus.BALANCED -> boost / 2
            else -> 0
        }
        val bowlingDelta = when (trainingFocus) {
            TrainingFocus.BOWLING -> boost
            TrainingFocus.BALANCED -> boost / 2
            else -> 0
        }
        val fieldingDelta = when (trainingFocus) {
            TrainingFocus.FIELDING, TrainingFocus.FITNESS -> boost
            TrainingFocus.BALANCED -> boost / 2
            else -> 0
        }

        return PlayerDevelopmentResult(
            playerId = player.id,
            battingSkillAfter = (player.battingSkill + battingDelta).coerceIn(1, 100),
            bowlingSkillAfter = (player.bowlingSkill + bowlingDelta).coerceIn(1, 100),
            fieldingSkillAfter = (player.fieldingSkill + fieldingDelta).coerceIn(1, 100)
        )
    }
}

enum class TrainingFocus { BATTING, BOWLING, FIELDING, FITNESS, BALANCED }
enum class TrainingIntensity { LIGHT, STANDARD, HARD, EXTREME }
