package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.MatchEntity
import kotlin.math.max

data class LeagueTableRow(
    val teamId: Long,
    val played: Int,
    val won: Int,
    val lost: Int,
    val points: Int,
    val runsFor: Int,
    val ballsFor: Int,
    val runsAgainst: Int,
    val ballsAgainst: Int
) {
    val netRunRate: Double
        get() {
            val forRate = if (ballsFor > 0) runsFor.toDouble() / (ballsFor / 6.0) else 0.0
            val againstRate = if (ballsAgainst > 0) runsAgainst.toDouble() / (ballsAgainst / 6.0) else 0.0
            return forRate - againstRate
        }
}

object LeagueEngine {
    fun rebuildTable(teamIds: List<Long>, matches: List<MatchEntity>): List<LeagueTableRow> {
        return teamIds.map { teamId ->
            val completed = matches.filter {
                it.status.name == "COMPLETED" && (it.team1Id == teamId || it.team2Id == teamId)
            }
            var won = 0
            var lost = 0
            var runsFor = 0
            var runsAgainst = 0
            var ballsFor = 0
            var ballsAgainst = 0

            completed.forEach { match ->
                val isTeam1 = match.team1Id == teamId
                if (match.winnerTeamId == teamId) won++ else if (match.winnerTeamId != null) lost++
                val ownRuns = if (isTeam1) match.inn1Runs else match.inn2Runs
                val oppRuns = if (isTeam1) match.inn2Runs else match.inn1Runs
                val ownBalls = if (isTeam1) match.inn1Balls else match.inn2Balls
                val oppBalls = if (isTeam1) match.inn2Balls else match.inn1Balls
                runsFor += ownRuns
                runsAgainst += oppRuns
                ballsFor += ownBalls
                ballsAgainst += oppBalls
            }

            LeagueTableRow(
                teamId = teamId,
                played = completed.size,
                won = won,
                lost = lost,
                points = won * 2,
                runsFor = runsFor,
                ballsFor = ballsFor,
                runsAgainst = runsAgainst,
                ballsAgainst = ballsAgainst
            )
        }.sortedWith(compareByDescending<LeagueTableRow> { it.points }.thenByDescending { it.netRunRate })
    }
}
