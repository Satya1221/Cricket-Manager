package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MatchStatus {
    UPCOMING,
    IN_PROGRESS,
    COMPLETED
}

enum class TossDecision {
    BAT,
    BOWL
}

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tournamentId: Long? = null,
    val team1Id: Long,
    val team2Id: Long,
    val oversPerInnings: Int = 20,
    val status: MatchStatus = MatchStatus.UPCOMING,
    val tossWinnerId: Long? = null,
    val tossDecision: TossDecision? = null,
    val battingFirstTeamId: Long? = null,
    val bowlingFirstTeamId: Long? = null,
    val currentInnings: Int = 1,
    // Innings 1 state
    val inn1Runs: Int = 0,
    val inn1Wickets: Int = 0,
    val inn1Balls: Int = 0,
    // Innings 2 state
    val inn2Runs: Int = 0,
    val inn2Wickets: Int = 0,
    val inn2Balls: Int = 0,
    val targetRuns: Int = 0,
    val winnerTeamId: Long? = null,
    val resultDescription: String = "",
    val manOfTheMatchPlayerId: Long? = null,
    val currentStrikerId: Long? = null,
    val currentNonStrikerId: Long? = null,
    val currentBowlerId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val currentRuns: Int
        get() = if (currentInnings == 1) inn1Runs else inn2Runs

    val currentWickets: Int
        get() = if (currentInnings == 1) inn1Wickets else inn2Wickets

    val currentBalls: Int
        get() = if (currentInnings == 1) inn1Balls else inn2Balls

    val currentOversFormatted: String
        get() {
            val overs = currentBalls / 6
            val balls = currentBalls % 6
            return "$overs.$balls"
        }

    val currentRunRate: Double
        get() = if (currentBalls > 0) (currentRuns.toDouble() / (currentBalls / 6.0)) else 0.0

    val requiredRunRate: Double
        get() {
            if (currentInnings != 2 || targetRuns <= 0) return 0.0
            val runsNeeded = targetRuns - inn2Runs
            val ballsRemaining = (oversPerInnings * 6) - inn2Balls
            return if (ballsRemaining > 0 && runsNeeded > 0) {
                (runsNeeded.toDouble() / (ballsRemaining / 6.0))
            } else 0.0
        }

    val battingTeamId: Long?
        get() = if (currentInnings == 1) battingFirstTeamId else bowlingFirstTeamId

    val bowlingTeamId: Long?
        get() = if (currentInnings == 1) bowlingFirstTeamId else battingFirstTeamId
}
