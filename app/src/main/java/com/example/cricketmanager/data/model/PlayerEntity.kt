package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlayerRole {
    BATSMAN,
    BOWLER,
    ALL_ROUNDER,
    WICKET_KEEPER
}

enum class BattingHand {
    RIGHT_HAND,
    LEFT_HAND
}

enum class BowlingStyle {
    RIGHT_ARM_FAST,
    RIGHT_ARM_MEDIUM,
    RIGHT_ARM_SPIN,
    LEFT_ARM_FAST,
    LEFT_ARM_SPIN
}

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teamId: Long,
    val name: String,
    val role: PlayerRole,
    val battingHand: BattingHand = BattingHand.RIGHT_HAND,
    val bowlingStyle: BowlingStyle = BowlingStyle.RIGHT_ARM_MEDIUM,
    val battingSkill: Int = 75,
    val bowlingSkill: Int = 60,
    val fieldingSkill: Int = 75,
    val jerseyNumber: Int = 7,
    val isCaptain: Boolean = false,
    val isWicketKeeper: Boolean = false,
    val inPlayingXi: Boolean = true,
    val battingOrder: Int = 1,
    // Career Stats
    val matchesPlayed: Int = 0,
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val highestScore: Int = 0,
    val centuries: Int = 0,
    val halfCenturies: Int = 0,
    val wicketsTaken: Int = 0,
    val oversBowledBalls: Int = 0,
    val runsConceded: Int = 0,
    val bestBowlingWickets: Int = 0,
    val bestBowlingRuns: Int = 0
) {
    val battingAverage: Double
        get() = if (matchesPlayed > 0) runsScored.toDouble() / matchesPlayed else 0.0

    val strikeRate: Double
        get() = if (ballsFaced > 0) (runsScored.toDouble() / ballsFaced) * 100 else 0.0

    val bowlingEconomy: Double
        get() = if (oversBowledBalls > 0) (runsConceded.toDouble() / (oversBowledBalls / 6.0)) else 0.0

    val oversFormatted: String
        get() {
            val overs = oversBowledBalls / 6
            val balls = oversBowledBalls % 6
            return "$overs.$balls"
        }
}
