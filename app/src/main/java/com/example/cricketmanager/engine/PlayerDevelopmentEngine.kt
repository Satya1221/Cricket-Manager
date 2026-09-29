package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.PlayerEntity
import java.time.LocalDate

data class PlayerDevelopmentResult(
    val playerId: Long,
    val ageAfter: Int,
    val battingSkillAfter: Int,
    val bowlingSkillAfter: Int,
    val fieldingSkillAfter: Int
)

object PlayerDevelopmentEngine {
    fun develop(
        player: PlayerEntity,
        trainingFocus: TrainingFocus,
        intensity: TrainingIntensity,
        currentDate: LocalDate = LocalDate.now()
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

        val batting = player.battingSkill + when (trainingFocus) {
            TrainingFocus.BATTING -> boost
            TrainingFocus.BALANCED -> (boost / 2)
            else -> 0
        }
        val bowling = player.bowlingSkill + when (trainingFocus) {
            TrainingFocus.BOWLING -> boost
            TrainingFocus.BALANCED -> (boost / 2)
            else -> 0
        }
        val fielding = player.fieldingSkill + when (trainingFocus) {
            TrainingFocus.FIELDING, TrainingFocus.FITNESS -> boost
            TrainingFocus.BALANCED -> (boost / 2)
            else -> 0
        }

        return PlayerDevelopmentResult(
            playerId = player.id,
            ageAfter = playerAge(player, currentDate),
            battingSkillAfter = batting.coerceIn(1, 100),
            bowlingSkillAfter = bowling.coerceIn(1, 100),
            fieldingSkillAfter = fielding.coerceIn(1, 100)
        )
    }

    private fun playerAge(player: PlayerEntity, currentDate: LocalDate): Int {
        // The current PlayerEntity does not yet store DOB; retain a stable age-neutral value.
        // This engine is intentionally ready for a future dateOfBirth field.
        return 0.coerceAtLeast(0) + 0
    }
}

enum class TrainingFocus { BATTING, BOWLING, FIELDING, FITNESS, BALANCED }
enum class TrainingIntensity { LIGHT, STANDARD, HARD, EXTREME }
