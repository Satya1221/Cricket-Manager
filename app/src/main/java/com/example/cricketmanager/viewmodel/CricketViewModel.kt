package com.example.cricketmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.ExtraType
import com.example.cricketmanager.data.model.MatchEntity
import com.example.cricketmanager.data.model.MatchStatus
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.PlayerRole
import com.example.cricketmanager.data.model.TeamEntity
import com.example.cricketmanager.data.model.TossDecision
import com.example.cricketmanager.data.model.TournamentEntity
import com.example.cricketmanager.data.model.TournamentStandingsEntity
import com.example.cricketmanager.data.model.WicketType
import com.example.cricketmanager.data.repository.CricketRepository
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.engine.MatchSimulationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CricketViewModel(private val repository: CricketRepository) : ViewModel() {

    val allTeams: StateFlow<List<TeamEntity>> = repository.allTeams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMatches: StateFlow<List<MatchEntity>> = repository.allMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMatch: StateFlow<MatchEntity?> = repository.activeMatch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentMatches: StateFlow<List<MatchEntity>> = repository.recentMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topRunScorers: StateFlow<List<PlayerEntity>> = repository.topRunScorers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topWicketTakers: StateFlow<List<PlayerEntity>> = repository.topWicketTakers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tournaments: StateFlow<List<TournamentEntity>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Match State
    private val _selectedMatchId = MutableStateFlow<Long?>(null)
    val selectedMatchId: StateFlow<Long?> = _selectedMatchId.asStateFlow()

    private val _currentMatch = MutableStateFlow<MatchEntity?>(null)
    val currentMatch: StateFlow<MatchEntity?> = _currentMatch.asStateFlow()

    private val _matchBalls = MutableStateFlow<List<BallEventEntity>>(emptyList())
    val matchBalls: StateFlow<List<BallEventEntity>> = _matchBalls.asStateFlow()

    private val _battingTeamPlayers = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val battingTeamPlayers: StateFlow<List<PlayerEntity>> = _battingTeamPlayers.asStateFlow()

    private val _bowlingTeamPlayers = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val bowlingTeamPlayers: StateFlow<List<PlayerEntity>> = _bowlingTeamPlayers.asStateFlow()

    // Match tactics state
    private val _battingMindset = MutableStateFlow(BattingMindset.BALANCED)
    val battingMindset: StateFlow<BattingMindset> = _battingMindset.asStateFlow()

    private val _bowlingPlan = MutableStateFlow(BowlingPlan.BALANCED)
    val bowlingPlan: StateFlow<BowlingPlan> = _bowlingPlan.asStateFlow()

    // Selected Team for Team Details
    private val _selectedTeam = MutableStateFlow<TeamEntity?>(null)
    val selectedTeam: StateFlow<TeamEntity?> = _selectedTeam.asStateFlow()

    private val _teamPlayers = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val teamPlayers: StateFlow<List<PlayerEntity>> = _teamPlayers.asStateFlow()

    // Standings for tournament
    private val _standings = MutableStateFlow<List<TournamentStandingsEntity>>(emptyList())
    val standings: StateFlow<List<TournamentStandingsEntity>> = _standings.asStateFlow()

    init {
        // Load initial tournament standings
        viewModelScope.launch {
            repository.allTournaments.collect { list ->
                if (list.isNotEmpty()) {
                    loadTournamentStandings(list.first().id)
                }
            }
        }
    }

    fun selectTeam(teamId: Long) {
        viewModelScope.launch {
            val team = repository.getTeamDirect(teamId)
            _selectedTeam.value = team
            repository.getPlayersForTeam(teamId).collect {
                _teamPlayers.value = it
            }
        }
    }

    fun loadTournamentStandings(tournamentId: Long) {
        viewModelScope.launch {
            repository.getStandings(tournamentId).collect {
                _standings.value = it
            }
        }
    }

    fun selectMatch(matchId: Long) {
        _selectedMatchId.value = matchId
        viewModelScope.launch {
            repository.getMatch(matchId).collect { match ->
                _currentMatch.value = match
                if (match != null) {
                    loadMatchParticipants(match)
                }
            }
        }
        viewModelScope.launch {
            repository.getBallsForMatch(matchId).collect { balls ->
                _matchBalls.value = balls
            }
        }
    }

    private suspend fun loadMatchParticipants(match: MatchEntity) {
        val battingId = match.battingTeamId ?: match.team1Id
        val bowlingId = match.bowlingTeamId ?: match.team2Id

        val batPlayers = repository.getPlayingXi(battingId)
        val bowlPlayers = repository.getPlayingXi(bowlingId)

        _battingTeamPlayers.value = batPlayers
        _bowlingTeamPlayers.value = bowlPlayers

        // If active match lacks strikers or bowler, initialize them
        if (match.status == MatchStatus.IN_PROGRESS) {
            var updated = match
            var needsUpdate = false

            if (updated.currentStrikerId == null && batPlayers.isNotEmpty()) {
                updated = updated.copy(currentStrikerId = batPlayers[0].id)
                needsUpdate = true
            }
            if (updated.currentNonStrikerId == null && batPlayers.size > 1) {
                updated = updated.copy(currentNonStrikerId = batPlayers[1].id)
                needsUpdate = true
            }
            if (updated.currentBowlerId == null && bowlPlayers.isNotEmpty()) {
                // Pick a bowler or all-rounder from the bottom/middle
                val preferredBowler = bowlPlayers.lastOrNull { it.role == PlayerRole.BOWLER } ?: bowlPlayers.last()
                updated = updated.copy(currentBowlerId = preferredBowler.id)
                needsUpdate = true
            }

            if (needsUpdate) {
                repository.updateMatch(updated)
                _currentMatch.value = updated
            }
        }
    }

    fun setBattingMindset(mindset: BattingMindset) {
        _battingMindset.value = mindset
    }

    fun setBowlingPlan(plan: BowlingPlan) {
        _bowlingPlan.value = plan
    }

    fun startNewMatch(
        team1Id: Long,
        team2Id: Long,
        overs: Int,
        tossWinnerId: Long,
        tossDecision: TossDecision,
        tournamentId: Long? = null
    ) {
        viewModelScope.launch {
            val battingFirst = if (tossDecision == TossDecision.BAT) tossWinnerId else {
                if (tossWinnerId == team1Id) team2Id else team1Id
            }
            val bowlingFirst = if (battingFirst == team1Id) team2Id else team1Id

            val batXi = repository.getPlayingXi(battingFirst)
            val bowlXi = repository.getPlayingXi(bowlingFirst)

            val striker = batXi.getOrNull(0)?.id
            val nonStriker = batXi.getOrNull(1)?.id
            val bowler = (bowlXi.lastOrNull { it.role == PlayerRole.BOWLER } ?: bowlXi.lastOrNull())?.id

            val newMatch = MatchEntity(
                tournamentId = tournamentId,
                team1Id = team1Id,
                team2Id = team2Id,
                oversPerInnings = overs,
                status = MatchStatus.IN_PROGRESS,
                tossWinnerId = tossWinnerId,
                tossDecision = tossDecision,
                battingFirstTeamId = battingFirst,
                bowlingFirstTeamId = bowlingFirst,
                currentInnings = 1,
                currentStrikerId = striker,
                currentNonStrikerId = nonStriker,
                currentBowlerId = bowler
            )

            val matchId = repository.insertMatch(newMatch)
            selectMatch(matchId)
        }
    }

    fun recordManualBall(
        runsBat: Int,
        extraType: ExtraType,
        extraRuns: Int,
        isWicket: Boolean,
        wicketType: WicketType
    ) {
        val match = _currentMatch.value ?: return
        if (match.status != MatchStatus.IN_PROGRESS) return

        val strikerId = match.currentStrikerId ?: return
        val nonStrikerId = match.currentNonStrikerId ?: return
        val bowlerId = match.currentBowlerId ?: return

        val striker = _battingTeamPlayers.value.find { it.id == strikerId } ?: return
        val nonStriker = _battingTeamPlayers.value.find { it.id == nonStrikerId } ?: return
        val bowler = _bowlingTeamPlayers.value.find { it.id == bowlerId } ?: return

        val currentBalls = match.currentBalls
        val overNumber = currentBalls / 6
        val ballNumberInOver = (currentBalls % 6) + 1

        val commentary = when {
            isWicket -> "OUT! $wicketType! ${striker.name} departs off ${bowler.name}'s bowling!"
            extraType == ExtraType.WIDE -> "Wide ball bowled by ${bowler.name}."
            extraType == ExtraType.NO_BALL -> "No ball signaled by umpire."
            runsBat == 4 -> "FOUR runs! Glorious boundary by ${striker.name}!"
            runsBat == 6 -> "SIX! Huge strike by ${striker.name} into the crowd!"
            runsBat == 0 -> "Defended cleanly by ${striker.name}. Dot ball."
            else -> "$runsBat run${if (runsBat > 1) "s" else ""} scored by ${striker.name}."
        }

        val ballEvent = BallEventEntity(
            matchId = match.id,
            inningsNumber = match.currentInnings,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            bowlerId = bowlerId,
            strikerId = strikerId,
            nonStrikerId = nonStrikerId,
            runsBat = runsBat,
            extraType = extraType,
            extraRuns = extraRuns,
            isWicket = isWicket,
            wicketType = wicketType,
            dismissedPlayerId = if (isWicket) strikerId else null,
            commentary = commentary
        )

        applyBallEvent(ballEvent, match, striker, nonStriker, bowler)
    }

    fun simulateNextBall() {
        val match = _currentMatch.value ?: return
        if (match.status != MatchStatus.IN_PROGRESS) return

        val strikerId = match.currentStrikerId ?: return
        val nonStrikerId = match.currentNonStrikerId ?: return
        val bowlerId = match.currentBowlerId ?: return

        val striker = _battingTeamPlayers.value.find { it.id == strikerId } ?: return
        val nonStriker = _battingTeamPlayers.value.find { it.id == nonStrikerId } ?: return
        val bowler = _bowlingTeamPlayers.value.find { it.id == bowlerId } ?: return

        val currentBalls = match.currentBalls
        val overNumber = currentBalls / 6
        val ballNumberInOver = (currentBalls % 6) + 1

        val simulatedBall = MatchSimulationEngine.simulateBall(
            matchId = match.id,
            inningsNumber = match.currentInnings,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            striker = striker,
            nonStriker = nonStriker,
            bowler = bowler,
            mindset = _battingMindset.value,
            bowlingPlan = _bowlingPlan.value
        )

        applyBallEvent(simulatedBall, match, striker, nonStriker, bowler)
    }

    fun simulateCurrentOver() {
        viewModelScope.launch {
            val match = _currentMatch.value ?: return@launch
            if (match.status != MatchStatus.IN_PROGRESS) return@launch
            val startBalls = match.currentBalls
            val ballsRemainingInOver = 6 - (startBalls % 6)
            for (i in 0 until ballsRemainingInOver) {
                val currentM = _currentMatch.value ?: break
                if (currentM.status != MatchStatus.IN_PROGRESS) break
                simulateNextBall()
            }
        }
    }

    fun autoSimulateInnings() {
        viewModelScope.launch {
            while (true) {
                val currentM = _currentMatch.value ?: break
                if (currentM.status != MatchStatus.IN_PROGRESS) break
                val targetBalls = currentM.oversPerInnings * 6
                if (currentM.currentBalls >= targetBalls || currentM.currentWickets >= 10) break
                simulateNextBall()
            }
        }
    }

    private fun applyBallEvent(
        ball: BallEventEntity,
        match: MatchEntity,
        striker: PlayerEntity,
        nonStriker: PlayerEntity,
        bowler: PlayerEntity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordBallEvent(ball)

            val totalRuns = ball.totalRuns
            val isLegal = ball.isLegalBall
            val isWicket = ball.isWicket

            val newBalls = if (isLegal) match.currentBalls + 1 else match.currentBalls
            val newRuns = match.currentRuns + totalRuns
            val newWickets = if (isWicket) match.currentWickets + 1 else match.currentWickets

            // Update player career stats
            val updatedStriker = striker.copy(
                runsScored = striker.runsScored + ball.runsBat,
                ballsFaced = if (ball.extraType != ExtraType.WIDE) striker.ballsFaced + 1 else striker.ballsFaced,
                fours = if (ball.runsBat == 4) striker.fours + 1 else striker.fours,
                sixes = if (ball.runsBat == 6) striker.sixes + 1 else striker.sixes,
                highestScore = maxOf(striker.highestScore, striker.runsScored + ball.runsBat)
            )
            val updatedBowler = bowler.copy(
                runsConceded = bowler.runsConceded + totalRuns,
                oversBowledBalls = if (isLegal) bowler.oversBowledBalls + 1 else bowler.oversBowledBalls,
                wicketsTaken = if (isWicket && ball.wicketType != WicketType.RUN_OUT) bowler.wicketsTaken + 1 else bowler.wicketsTaken
            )
            repository.updatePlayer(updatedStriker)
            repository.updatePlayer(updatedBowler)

            // Calculate next striker & non-striker
            var nextStrikerId = match.currentStrikerId
            var nextNonStrikerId = match.currentNonStrikerId
            var nextBowlerId = match.currentBowlerId

            if (isWicket) {
                // Find next batsman from batting XI
                val allBatting = _battingTeamPlayers.value
                val battedIds = (_matchBalls.value.map { it.strikerId } + _matchBalls.value.map { it.nonStrikerId }).toSet() + striker.id
                val nextBatter = allBatting.firstOrNull { it.id !in battedIds && it.id != nextNonStrikerId }
                nextStrikerId = nextBatter?.id
            } else {
                // Strike rotation: odd runs switch strike
                if (ball.runsBat % 2 == 1 || (ball.extraType == ExtraType.WIDE && ball.extraRuns % 2 == 1)) {
                    val temp = nextStrikerId
                    nextStrikerId = nextNonStrikerId
                    nextNonStrikerId = temp
                }
            }

            // End of over handling
            val overCompleted = isLegal && (newBalls % 6 == 0)
            if (overCompleted) {
                // Switch ends
                val temp = nextStrikerId
                nextStrikerId = nextNonStrikerId
                nextNonStrikerId = temp

                // Pick another bowler from bowling XI
                val bowlingXI = _bowlingTeamPlayers.value
                val eligibleBowlers = bowlingXI.filter { it.id != bowler.id }
                if (eligibleBowlers.isNotEmpty()) {
                    val nextB = eligibleBowlers.filter { it.role == PlayerRole.BOWLER || it.role == PlayerRole.ALL_ROUNDER }
                        .randomOrNull() ?: eligibleBowlers.random()
                    nextBowlerId = nextB.id
                }
            }

            // Check Innings or Match Completion
            val maxBalls = match.oversPerInnings * 6
            val allOut = newWickets >= 10 || nextStrikerId == null

            if (match.currentInnings == 1) {
                if (newBalls >= maxBalls || allOut) {
                    // Innings 1 finished! Switch to Innings 2
                    val target = newRuns + 1
                    val newBattingTeamId = match.bowlingFirstTeamId ?: match.team2Id
                    val newBowlingTeamId = match.battingFirstTeamId ?: match.team1Id

                    val newBatXi = repository.getPlayingXi(newBattingTeamId)
                    val newBowlXi = repository.getPlayingXi(newBowlingTeamId)

                    val inn2Striker = newBatXi.getOrNull(0)?.id
                    val inn2NonStriker = newBatXi.getOrNull(1)?.id
                    val inn2Bowler = (newBowlXi.lastOrNull { it.role == PlayerRole.BOWLER } ?: newBowlXi.lastOrNull())?.id

                    val updatedMatch = match.copy(
                        inn1Runs = newRuns,
                        inn1Wickets = newWickets,
                        inn1Balls = newBalls,
                        targetRuns = target,
                        currentInnings = 2,
                        currentStrikerId = inn2Striker,
                        currentNonStrikerId = inn2NonStriker,
                        currentBowlerId = inn2Bowler
                    )
                    repository.updateMatch(updatedMatch)
                    _currentMatch.value = updatedMatch
                    loadMatchParticipants(updatedMatch)
                    return@launch
                } else {
                    val updatedMatch = match.copy(
                        inn1Runs = newRuns,
                        inn1Wickets = newWickets,
                        inn1Balls = newBalls,
                        currentStrikerId = nextStrikerId,
                        currentNonStrikerId = nextNonStrikerId,
                        currentBowlerId = nextBowlerId
                    )
                    repository.updateMatch(updatedMatch)
                    _currentMatch.value = updatedMatch
                }
            } else {
                // Innings 2 (chasing)
                val target = match.targetRuns
                val chasedSuccessfully = newRuns >= target
                val innings2Finished = chasedSuccessfully || newBalls >= maxBalls || allOut

                if (innings2Finished) {
                    val team1 = repository.getTeamDirect(match.team1Id)
                    val team2 = repository.getTeamDirect(match.team2Id)
                    val battingTeam = if (match.battingTeamId == match.team1Id) team1 else team2
                    val bowlingTeam = if (battingTeam?.id == team1?.id) team2 else team1

                    val (winnerId, resultDesc) = when {
                        newRuns >= target -> {
                            val wicketsRemaining = 10 - newWickets
                            val ballsRemaining = maxBalls - newBalls
                            battingTeam?.id to "${battingTeam?.name} won by $wicketsRemaining wicket${if (wicketsRemaining > 1) "s" else ""} ($ballsRemaining balls remaining)"
                        }
                        newRuns == target - 1 -> {
                            null to "Match Tied!"
                        }
                        else -> {
                            val runsDeficit = (target - 1) - newRuns
                            bowlingTeam?.id to "${bowlingTeam?.name} won by $runsDeficit run${if (runsDeficit > 1) "s" else ""}"
                        }
                    }

                    val updatedMatch = match.copy(
                        inn2Runs = newRuns,
                        inn2Wickets = newWickets,
                        inn2Balls = newBalls,
                        status = MatchStatus.COMPLETED,
                        winnerTeamId = winnerId,
                        resultDescription = resultDesc
                    )
                    repository.updateMatch(updatedMatch)
                    _currentMatch.value = updatedMatch

                    // Update tournament standings if part of a tournament
                    if (match.tournamentId != null && winnerId != null) {
                        updateTournamentStandingsAfterMatch(match.tournamentId, winnerId, if (winnerId == match.team1Id) match.team2Id else match.team1Id)
                    }
                } else {
                    val updatedMatch = match.copy(
                        inn2Runs = newRuns,
                        inn2Wickets = newWickets,
                        inn2Balls = newBalls,
                        currentStrikerId = nextStrikerId,
                        currentNonStrikerId = nextNonStrikerId,
                        currentBowlerId = nextBowlerId
                    )
                    repository.updateMatch(updatedMatch)
                    _currentMatch.value = updatedMatch
                }
            }
        }
    }

    private suspend fun updateTournamentStandingsAfterMatch(tournamentId: Long, winnerId: Long, loserId: Long) {
        val winnerStanding = repository.getTeamStanding(tournamentId, winnerId)
        val loserStanding = repository.getTeamStanding(tournamentId, loserId)

        if (winnerStanding != null) {
            repository.updateStanding(
                winnerStanding.copy(
                    played = winnerStanding.played + 1,
                    won = winnerStanding.won + 1,
                    points = winnerStanding.points + 2,
                    netRunRate = winnerStanding.netRunRate + 0.35
                )
            )
        }
        if (loserStanding != null) {
            repository.updateStanding(
                loserStanding.copy(
                    played = loserStanding.played + 1,
                    lost = loserStanding.lost + 1,
                    netRunRate = loserStanding.netRunRate - 0.35
                )
            )
        }
        loadTournamentStandings(tournamentId)
    }

    fun undoLastBall() {
        val match = _currentMatch.value ?: return
        if (match.status != MatchStatus.IN_PROGRESS) return

        viewModelScope.launch(Dispatchers.IO) {
            val lastBall = repository.getLastBall(match.id) ?: return@launch
            repository.undoLastBall(match.id)

            val isLegal = lastBall.isLegalBall
            val totalRuns = lastBall.totalRuns
            val wasWicket = lastBall.isWicket

            if (match.currentInnings == 1) {
                val updatedMatch = match.copy(
                    inn1Runs = maxOf(0, match.inn1Runs - totalRuns),
                    inn1Balls = if (isLegal) maxOf(0, match.inn1Balls - 1) else match.inn1Balls,
                    inn1Wickets = if (wasWicket) maxOf(0, match.inn1Wickets - 1) else match.inn1Wickets
                )
                repository.updateMatch(updatedMatch)
                _currentMatch.value = updatedMatch
            } else {
                val updatedMatch = match.copy(
                    inn2Runs = maxOf(0, match.inn2Runs - totalRuns),
                    inn2Balls = if (isLegal) maxOf(0, match.inn2Balls - 1) else match.inn2Balls,
                    inn2Wickets = if (wasWicket) maxOf(0, match.inn2Wickets - 1) else match.inn2Wickets
                )
                repository.updateMatch(updatedMatch)
                _currentMatch.value = updatedMatch
            }
        }
    }

    fun createTeam(name: String, shortCode: String, city: String, primaryColorHex: String, secondaryColorHex: String) {
        viewModelScope.launch {
            val team = TeamEntity(
                name = name,
                shortCode = shortCode.uppercase(),
                city = city,
                primaryColorHex = primaryColorHex,
                secondaryColorHex = secondaryColorHex,
                isCustom = true
            )
            val teamId = repository.insertTeam(team)
            // Add initial 11 default players
            val players = (1..11).map { num ->
                PlayerEntity(
                    teamId = teamId,
                    name = "$shortCode Player $num",
                    role = when (num) {
                        in 1..4 -> PlayerRole.BATSMAN
                        5 -> PlayerRole.WICKET_KEEPER
                        in 6..7 -> PlayerRole.ALL_ROUNDER
                        else -> PlayerRole.BOWLER
                    },
                    jerseyNumber = num,
                    isCaptain = num == 1,
                    isWicketKeeper = num == 5,
                    battingOrder = num
                )
            }
            players.forEach { repository.insertPlayer(it) }
        }
    }

    fun addPlayer(
        teamId: Long,
        name: String,
        role: PlayerRole,
        battingSkill: Int,
        bowlingSkill: Int,
        jerseyNumber: Int
    ) {
        viewModelScope.launch {
            val player = PlayerEntity(
                teamId = teamId,
                name = name,
                role = role,
                battingSkill = battingSkill,
                bowlingSkill = bowlingSkill,
                jerseyNumber = jerseyNumber,
                battingOrder = _teamPlayers.value.size + 1
            )
            repository.insertPlayer(player)
            selectTeam(teamId)
        }
    }

    fun togglePlayingXi(player: PlayerEntity) {
        viewModelScope.launch {
            repository.updatePlayer(player.copy(inPlayingXi = !player.inPlayingXi))
            selectTeam(player.teamId)
        }
    }

    fun setCaptain(player: PlayerEntity) {
        viewModelScope.launch {
            val currentCaptain = _teamPlayers.value.find { it.isCaptain }
            if (currentCaptain != null) {
                repository.updatePlayer(currentCaptain.copy(isCaptain = false))
            }
            repository.updatePlayer(player.copy(isCaptain = true))
            selectTeam(player.teamId)
        }
    }

    fun setWicketKeeper(player: PlayerEntity) {
        viewModelScope.launch {
            val currentKeeper = _teamPlayers.value.find { it.isWicketKeeper }
            if (currentKeeper != null) {
                repository.updatePlayer(currentKeeper.copy(isWicketKeeper = false))
            }
            repository.updatePlayer(player.copy(isWicketKeeper = true))
            selectTeam(player.teamId)
        }
    }
}

class CricketViewModelFactory(private val repository: CricketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CricketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CricketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
