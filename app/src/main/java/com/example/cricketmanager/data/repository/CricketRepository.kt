package com.example.cricketmanager.data.repository

import com.example.cricketmanager.data.dao.*
import com.example.cricketmanager.data.model.*
import kotlinx.coroutines.flow.Flow

class CricketRepository(
    private val teamDao: TeamDao,
    private val playerDao: PlayerDao,
    private val matchDao: MatchDao,
    private val ballEventDao: BallEventDao,
    private val tournamentDao: TournamentDao,
    private val contractDao: ContractDao? = null,
    private val developmentDao: DevelopmentDao? = null,
    private val financialTransactionDao: FinancialTransactionDao? = null
) {
    val allTeams = teamDao.getAllTeams()
    val allMatches = matchDao.getAllMatches()
    val activeMatch = matchDao.getActiveMatch()
    val recentMatches = matchDao.getRecentMatches(5)
    val topRunScorers = playerDao.getTopRunScorers(10)
    val topWicketTakers = playerDao.getTopWicketTakers(10)
    val allTournaments = tournamentDao.getAllTournaments()

    fun getTeam(id: Long): Flow<TeamEntity?> = teamDao.getTeamByIdFlow(id)
    suspend fun getTeamDirect(id: Long) = teamDao.getTeamById(id)
    fun getPlayersForTeam(teamId: Long) = playerDao.getPlayersForTeam(teamId)
    suspend fun getPlayingXi(teamId: Long) = playerDao.getPlayingXi(teamId)
    suspend fun getPlayer(id: Long) = playerDao.getPlayerById(id)
    suspend fun getPlayersByIds(ids: List<Long>) = playerDao.getPlayersByIds(ids)
    fun getMatch(matchId: Long) = matchDao.getMatchByIdFlow(matchId)
    suspend fun getMatchDirect(matchId: Long) = matchDao.getMatchById(matchId)
    fun getMatchesForTournament(tournamentId: Long) = matchDao.getMatchesForTournament(tournamentId)
    fun getBallsForMatch(matchId: Long) = ballEventDao.getBallsForMatch(matchId)
    fun getBallsForInnings(matchId: Long, innings: Int) = ballEventDao.getBallsForInnings(matchId, innings)
    suspend fun getBallsForInningsList(matchId: Long, innings: Int) = ballEventDao.getBallsForInningsList(matchId, innings)
    fun getRecentBalls(matchId: Long, limit: Int = 12) = ballEventDao.getRecentBalls(matchId, limit)
    fun getStandings(tournamentId: Long) = tournamentDao.getStandingsForTournament(tournamentId)

    fun getContractsForTeam(teamId: Long) = requireNotNull(contractDao).getContractsForTeam(teamId)
    fun getAllContracts() = requireNotNull(contractDao).getAllContracts()
    suspend fun getContract(playerId: Long) = requireNotNull(contractDao).getContractForPlayer(playerId)
    suspend fun saveContract(contract: ContractEntity) = requireNotNull(contractDao).insert(contract)
    suspend fun updateContract(contract: ContractEntity) = requireNotNull(contractDao).update(contract)
    fun observeDevelopment(playerId: Long) = requireNotNull(developmentDao).observe(playerId)
    fun observeAllDevelopment() = requireNotNull(developmentDao).observeAll()
    suspend fun getDevelopment(playerId: Long) = requireNotNull(developmentDao).get(playerId)
    suspend fun saveDevelopment(entity: PlayerDevelopmentEntity) = requireNotNull(developmentDao).insert(entity)
    fun observeTransactions(teamId: Long) = requireNotNull(financialTransactionDao).observeForTeam(teamId)
    suspend fun recordTransaction(transaction: FinancialTransactionEntity) = requireNotNull(financialTransactionDao).insert(transaction)

    suspend fun insertTeam(team: TeamEntity) = teamDao.insertTeam(team)
    suspend fun updateTeam(team: TeamEntity) = teamDao.updateTeam(team)
    suspend fun deleteTeam(team: TeamEntity) = teamDao.deleteTeam(team)
    suspend fun insertPlayer(player: PlayerEntity) = playerDao.insertPlayer(player)
    suspend fun updatePlayer(player: PlayerEntity) = playerDao.updatePlayer(player)
    suspend fun updatePlayers(players: List<PlayerEntity>) = playerDao.updatePlayers(players)
    suspend fun deletePlayer(player: PlayerEntity) = playerDao.deletePlayer(player)
    suspend fun insertMatch(match: MatchEntity) = matchDao.insertMatch(match)
    suspend fun updateMatch(match: MatchEntity) = matchDao.updateMatch(match)
    suspend fun deleteMatch(match: MatchEntity) = matchDao.deleteMatch(match)
    suspend fun recordBallEvent(ball: BallEventEntity) = ballEventDao.insertBall(ball)
    suspend fun undoLastBall(matchId: Long) = ballEventDao.deleteLastBall(matchId)
    suspend fun getLastBall(matchId: Long) = ballEventDao.getLastBall(matchId)
    suspend fun deleteAllBallsForMatch(matchId: Long) = ballEventDao.deleteAllBallsForMatch(matchId)
    suspend fun updateStanding(standing: TournamentStandingsEntity) = tournamentDao.updateStanding(standing)
    suspend fun getTeamStanding(tournamentId: Long, teamId: Long) = tournamentDao.getTeamStanding(tournamentId, teamId)
}
