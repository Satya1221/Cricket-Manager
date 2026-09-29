package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.data.repository.CricketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.random.Random

/** Headless fast simulation for AI-vs-AI matches. No Compose work is performed here. */
class FastSimEngine(
    private val repository: CricketRepository,
    private val rng: Random = Random.Default
) {
    suspend fun simulateHeadlessMatch(match: MatchEntity): MatchEntity = withContext(Dispatchers.Default) {
        val team1 = repository.getPlayingXi(match.team1Id)
        val team2 = repository.getPlayingXi(match.team2Id)
        if (team1.isEmpty() || team2.isEmpty()) return@withContext match

        val first = simulateInnings(team1, team2, match.oversPerInnings)
        val second = simulateInnings(team2, team1, match.oversPerInnings, first.runs + 1)
        val winner = when {
            second.runs >= first.runs + 1 -> match.team2Id
            first.runs > second.runs -> match.team1Id
            else -> null
        }
        val result = when {
            winner == match.team1Id -> "${match.team1Id} won by ${first.runs - second.runs} runs"
            winner == match.team2Id -> "${match.team2Id} won by ${10 - second.wickets} wickets"
            else -> "Match tied"
        }
        match.copy(
            status = MatchStatus.COMPLETED,
            inn1Runs = first.runs,
            inn1Wickets = first.wickets,
            inn1Balls = first.balls,
            inn2Runs = second.runs,
            inn2Wickets = second.wickets,
            inn2Balls = second.balls,
            targetRuns = first.runs + 1,
            winnerTeamId = winner,
            resultDescription = result
        )
    }

    suspend fun autoSimulateLeagueRound(matches: List<MatchEntity>): List<MatchEntity> =
        withContext(Dispatchers.Default) {
            coroutineScope {
                matches.map { match ->
                    async { simulateHeadlessMatch(match) }
                }.awaitAll().also { results ->
                    // Persist only after simulation work has completed off the UI thread.
                    results.forEach { repository.updateMatch(it) }
                }
            }
        }

    private data class InningsResult(val runs: Int, val wickets: Int, val balls: Int)

    private suspend fun simulateInnings(
        batting: List<com.example.cricketmanager.data.model.PlayerEntity>,
        bowling: List<com.example.cricketmanager.data.model.PlayerEntity>,
        overs: Int,
        target: Int? = null
    ): InningsResult {
        var runs = 0
        var wickets = 0
        var balls = 0
        var strikerIndex = 0
        var nonStrikerIndex = 1
        val maxBalls = overs * 6
        while (balls < maxBalls && wickets < 10 && (target == null || runs < target)) {
            val striker = batting.getOrElse(strikerIndex) { batting.last() }
            val nonStriker = batting.getOrElse(nonStrikerIndex) { batting.last() }
            val bowler = bowling[(balls / 6) % bowling.size]
            val p = MatchSimulationEngine.outcomeProbabilities(
                striker = striker,
                bowler = bowler,
                mindset = BattingMindset.BALANCED,
                bowlingPlan = BowlingPlan.BALANCED,
                requiredRunRate = if (target != null && balls < maxBalls) {
                    (target - runs).coerceAtLeast(0) / ((maxBalls - balls) / 6.0)
                } else 8.0,
                ballsRemaining = maxBalls - balls,
                wicketsLost = wickets
            )
            val roll = rng.nextDouble()
            var cursor = p.zero
            val outcomeRuns = when {
                roll < cursor -> 0
                roll < (cursor + p.one).also { cursor += p.one } -> 1
                roll < (cursor + p.two).also { cursor += p.two } -> 2
                roll < (cursor + p.three).also { cursor += p.three } -> 3
                roll < (cursor + p.four).also { cursor += p.four } -> 4
                roll < (cursor + p.six).also { cursor += p.six } -> 6
                else -> -1 // wicket
            }
            if (outcomeRuns < 0) {
                wickets++
                strikerIndex = max(strikerIndex + 2, 0)
                if (strikerIndex >= batting.size) break
            } else {
                runs += outcomeRuns
                if (outcomeRuns % 2 == 1) {
                    val tmp = strikerIndex
                    strikerIndex = nonStrikerIndex
                    nonStrikerIndex = tmp
                }
            }
            balls++
            if (balls % 6 == 0) {
                val tmp = strikerIndex
                strikerIndex = nonStrikerIndex
                nonStrikerIndex = tmp
            }
        }
        return InningsResult(runs, wickets, balls)
    }
}
