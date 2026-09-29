package com.example.cricketmanager.data.repository

import com.example.cricketmanager.data.dao.BallEventDao
import com.example.cricketmanager.data.dao.MatchDao
import com.example.cricketmanager.data.dao.PlayerDao
import com.example.cricketmanager.data.dao.TeamDao
import com.example.cricketmanager.data.dao.TournamentDao
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.TournamentEntity
import com.example.cricketmanager.data.model.TournamentStandingsEntity
import kotlinx.coroutines.flow.Flow

class CricketRepository(
    private val teamDao: TeamDao,
    private val playerDao: PlayerDao,
    private val matchDao: MatchDao,
    private val ballEventDao: BallEventDao,
    private val tournamentDao: TournamentDao
) {
    val allTeams: Flow<List<TeamEntity>> = teamDao.getAllTeams()
    val allMatches: Flow<List<MatchEntity>> = matchDao.getAllMatches()
    val activeMatch: Flow<MatchEntity?> = matchDao.getActiveMatch()
    val recentMatches: Flow<List<MatchEntity>> = matchDao.getRecentMatches(5)
    val topRunScorers: Flow<List<PlayerEntity>> = playerDao.getTopRunScorers(10)
    val topWicketTakers: Flow<List<PlayerEntity>> = playerDao.getTopWicketTakers(10)
    val allTournaments: Flow<List<TournamentEntity>> = tournamentDao.getAllTournaments()

    fun getTeam(id: Long): Flow<TeamEntity?> = teamDao.getTeamByIdFlow(id)
    suspend fun getTeamDirect(id: Long): TeamEntity? = teamDao.getTeamById(id)

    fun getPlayersForTeam(teamId: Long): Flow<List<PlayerEntity>> = playerDao.getPlayersForTeam(teamId)
    suspend fun getPlayingXi(teamId: Long): List<PlayerEntity> = playerDao.getPlayingXi(teamId)
    suspend fun getPlayer(id: Long): PlayerEntity? = playerDao.getPlayerById(id)
    suspend fun getPlayersByIds(ids: List<Long>): List<PlayerEntity> = playerDao.getPlayersByIds(ids)

    fun getMatch(matchId: Long): Flow<MatchEntity?> = matchDao.getMatchByIdFlow(matchId)
    suspend fun getMatchDirect(matchId: Long): MatchEntity? = matchDao.getMatchById(matchId)
    fun getMatchesForTournament(tournamentId: Long): Flow<List<MatchEntity>> = matchDao.getMatchesForTournament(tournamentId)

    fun getBallsForMatch(matchId: Long): Flow<List<BallEventEntity>> = ballEventDao.getBallsForMatch(matchId)
    fun getBallsForInnings(matchId: Long, innings: Int): Flow<List<BallEventEntity>> = ballEventDao.getBallsForInnings(matchId, innings)
    suspend fun getBallsForInningsList(matchId: Long, innings: Int): List<BallEventEntity> = ballEventDao.getBallsForInningsList(matchId, innings)
    fun getRecentBalls(matchId: Long, limit: Int = 12): Flow<List<BallEventEntity>> = ballEventDao.getRecentBalls(matchId, limit)

    fun getStandings(tournamentId: Long): Flow<List<TournamentStandingsEntity>> = tournamentDao.getStandingsForTournament(tournamentId)

    suspend fun insertTeam(team: TeamEntity): Long = teamDao.insertTeam(team)
    suspend fun updateTeam(team: TeamEntity) = teamDao.updateTeam(team)
    suspend fun deleteTeam(team: TeamEntity) = teamDao.deleteTeam(team)

    suspend fun insertPlayer(player: PlayerEntity): Long = playerDao.insertPlayer(player)
    suspend fun updatePlayer(player: PlayerEntity) = playerDao.updatePlayer(player)
    suspend fun updatePlayers(players: List<PlayerEntity>) = playerDao.updatePlayers(players)
    suspend fun deletePlayer(player: PlayerEntity) = playerDao.deletePlayer(player)

    suspend fun insertMatch(match: MatchEntity): Long = matchDao.insertMatch(match)
    suspend fun updateMatch(match: MatchEntity) = matchDao.updateMatch(match)
    suspend fun deleteMatch(match: MatchEntity) = matchDao.deleteMatch(match)

    suspend fun recordBallEvent(ball: BallEventEntity): Long = ballEventDao.insertBall(ball)
    suspend fun undoLastBall(matchId: Long): Int = ballEventDao.deleteLastBall(matchId)
    suspend fun getLastBall(matchId: Long): BallEventEntity? = ballEventDao.getLastBall(matchId)
    suspend fun deleteAllBallsForMatch(matchId: Long) = ballEventDao.deleteAllBallsForMatch(matchId)

    suspend fun updateStanding(standing: TournamentStandingsEntity) = tournamentDao.updateStanding(standing)
    suspend fun getTeamStanding(tournamentId: Long, teamId: Long) = tournamentDao.getTeamStanding(tournamentId, teamId)
}
