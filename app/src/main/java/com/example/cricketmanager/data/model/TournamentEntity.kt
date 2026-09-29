package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "Premier Cricket League",
    val season: String = "2026",
    val overs: Int = 20,
    val isCompleted: Boolean = false,
    val championTeamId: Long? = null
)

@Entity(tableName = "tournament_standings")
data class TournamentStandingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tournamentId: Long,
    val teamId: Long,
    val played: Int = 0,
    val won: Int = 0,
    val lost: Int = 0,
    val tied: Int = 0,
    val points: Int = 0,
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val runsConceded: Int = 0,
    val ballsBowled: Int = 0,
    val netRunRate: Double = 0.0
)
