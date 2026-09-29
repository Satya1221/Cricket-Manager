package com.example.cricketmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cricketmanager.data.model.*
import com.example.cricketmanager.data.repository.CricketRepository
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.engine.MatchSimulationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CricketViewModel(val repository: CricketRepository) : ViewModel() {

    // Teams & Matches flows from Repository
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

    // Field preset for 3D tactics
    private val _fieldPreset = MutableStateFlow("Balanced Ring")
    val fieldPreset: StateFlow<String> = _fieldPreset.asStateFlow()

    // Selected Team for Team Details & Management
    private val _selectedTeam = MutableStateFlow<TeamEntity?>(null)
    val selectedTeam: StateFlow<TeamEntity?> = _selectedTeam.asStateFlow()

    private val _teamPlayers = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val teamPlayers: StateFlow<List<PlayerEntity>> = _teamPlayers.asStateFlow()

    // Standings for tournament
    private val _standings = MutableStateFlow<List<TournamentStandingsEntity>>(emptyList())
    val standings: StateFlow<List<TournamentStandingsEntity>> = _standings.asStateFlow()

    // Franchise Career & Finances
    val seasonNumber = MutableStateFlow(1)
    val userPurseLakhs = MutableStateFlow(6850) // ₹68.50 Crores
    val userTeamId = MutableStateFlow(1L) // Default to Team 1 (e.g. Mumbai)

    // ==========================================
    // YOUTH ACADEMY SYSTEM
    // ==========================================
    private val _academyLevel = MutableStateFlow(2)
    val academyLevel: StateFlow<Int> = _academyLevel.asStateFlow()

    private val _youthPlayers = MutableStateFlow(
        listOf(
            YouthPlayer("YP_1", "Aarav Sharma", 17, PlayerRole.BATSMAN, 66, 94, 72, 38, "Aggressive Opener", 0.65f),
            YouthPlayer("YP_2", "Devendra Rawat", 18, PlayerRole.BOWLER, 68, 92, 32, 75, "145 kph Express Pace", 0.50f),
            YouthPlayer("YP_3", "Karan Singhal", 16, PlayerRole.ALL_ROUNDER, 64, 91, 65, 68, "Hard-hitting Finisher", 0.40f),
            YouthPlayer("YP_4", "Manish Iyer", 17, PlayerRole.WICKET_KEEPER, 65, 88, 70, 25, "Lightning Glovework", 0.70f),
            YouthPlayer("YP_5", "Pranav Deshmukh", 18, PlayerRole.BOWLER, 63, 89, 28, 72, "Mystery Leg Spin", 0.30f),
            YouthPlayer("YP_6", "Vikramaditya Roy", 19, PlayerRole.BATSMAN, 70, 93, 76, 40, "Classical Top Order", 0.85f)
        )
    )
    val youthPlayers: StateFlow<List<YouthPlayer>> = _youthPlayers.asStateFlow()

    fun trainYouthPlayer(youthId: String) {
        _youthPlayers.value = _youthPlayers.value.map { yp ->
            if (yp.id == youthId && !yp.isPromoted) {
                val newProgress = (yp.progress + 0.25f).coerceAtMost(1f)
                val newSkill = if (newProgress >= 1f) (yp.currentSkill + 3).coerceAtMost(yp.potentialSkill) else yp.currentSkill
                val newBat = if (yp.role == PlayerRole.BATSMAN || yp.role == PlayerRole.ALL_ROUNDER) yp.battingSkill + 2 else yp.battingSkill
                val newBowl = if (yp.role == PlayerRole.BOWLER || yp.role == PlayerRole.ALL_ROUNDER) yp.bowlingSkill + 2 else yp.bowlingSkill
                yp.copy(
                    progress = if (newProgress >= 1f) 0.1f else newProgress,
                    currentSkill = newSkill,
                    battingSkill = newBat,
                    bowlingSkill = newBowl
                )
            } else yp
        }
    }

    fun upgradeAcademy() {
        if (_academyLevel.value < 5 && userPurseLakhs.value >= 200) {
            userPurseLakhs.value -= 200
            _academyLevel.value += 1
        }
    }

    fun promoteYouthToSenior(youthId: String, targetTeamId: Long) {
        val yp = _youthPlayers.value.find { it.id == youthId } ?: return
        if (yp.isPromoted) return

        viewModelScope.launch {
            val newPlayer = PlayerEntity(
                teamId = targetTeamId,
                name = yp.name,
                role = yp.role,
                battingSkill = yp.battingSkill + 4,
                bowlingSkill = yp.bowlingSkill + 4,
                fieldingSkill = 75,
                jerseyNumber = (12..99).random(),
                inPlayingXi = false,
                battingOrder = 12
            )
            repository.insertPlayer(newPlayer)

            // Mark as promoted
            _youthPlayers.value = _youthPlayers.value.map {
                if (it.id == youthId) it.copy(isPromoted = true) else it
            }

            selectTeam(targetTeamId)
        }
    }

    // ==========================================
    // AUCTION SYSTEM
    // "make auction not available as an option directly
    //  it will show up when season starts everytime"
    // ==========================================
    private val _isSeasonAuctionActive = MutableStateFlow(false)
    val isSeasonAuctionActive: StateFlow<Boolean> = _isSeasonAuctionActive.asStateFlow()

    private val _auctionPlayersPool = MutableStateFlow(
        listOf(
            AuctionItem("AUC_1", "Heinrich Klaasen", PlayerRole.WICKET_KEEPER, 91, 32, "South Africa", 200, 200),
            AuctionItem("AUC_2", "Mitchell Starc", PlayerRole.BOWLER, 92, 34, "Australia", 200, 200),
            AuctionItem("AUC_3", "Travis Head", PlayerRole.BATSMAN, 90, 30, "Australia", 200, 200),
            AuctionItem("AUC_4", "Rinku Singh", PlayerRole.BATSMAN, 87, 26, "India", 150, 150),
            AuctionItem("AUC_5", "Gerald Coetzee", PlayerRole.BOWLER, 86, 23, "South Africa", 100, 100),
            AuctionItem("AUC_6", "Cameron Green", PlayerRole.ALL_ROUNDER, 88, 25, "Australia", 200, 200),
            AuctionItem("AUC_7", "Mayank Yadav", PlayerRole.BOWLER, 85, 22, "India", 50, 50),
            AuctionItem("AUC_8", "Rachin Ravindra", PlayerRole.ALL_ROUNDER, 86, 24, "New Zealand", 100, 100),
            AuctionItem("AUC_9", "Matheesha Pathirana", PlayerRole.BOWLER, 88, 21, "Sri Lanka", 100, 100),
            AuctionItem("AUC_10", "Phil Salt", PlayerRole.BATSMAN, 87, 27, "England", 150, 150)
        )
    )
    val auctionPlayersPool: StateFlow<List<AuctionItem>> = _auctionPlayersPool.asStateFlow()

    private val _currentAuctionIndex = MutableStateFlow(0)
    val currentAuctionIndex: StateFlow<Int> = _currentAuctionIndex.asStateFlow()

    private val _auctionTimer = MutableStateFlow(10)
    val auctionTimer: StateFlow<Int> = _auctionTimer.asStateFlow()

    fun triggerSeasonStartAuction() {
        _isSeasonAuctionActive.value = true
        _currentAuctionIndex.value = 0
        _auctionTimer.value = 12
        resetAuctionBids()
    }

    private fun resetAuctionBids() {
        val pool = _auctionPlayersPool.value
        pool.forEach { item ->
            item.currentBidLakhs = item.basePriceLakhs
            item.highestBidderTeam = "None"
            item.highestBidderId = null
            item.isSold = false
            item.isPassed = false
        }
        _auctionPlayersPool.value = ArrayList(pool)
    }

    fun placeUserBid() {
        val idx = _currentAuctionIndex.value
        val pool = _auctionPlayersPool.value
        if (idx >= pool.size) return
        val currentItem = pool[idx]

        val raiseIncrement = if (currentItem.currentBidLakhs >= 1000) 50 else if (currentItem.currentBidLakhs >= 200) 25 else 20
        val newBid = currentItem.currentBidLakhs + raiseIncrement

        if (userPurseLakhs.value >= newBid) {
            val userTeam = allTeams.value.find { it.id == userTeamId.value }
            currentItem.currentBidLakhs = newBid
            currentItem.highestBidderTeam = userTeam?.name ?: "My Franchise"
            currentItem.highestBidderId = userTeamId.value
            _auctionTimer.value = 8
            _auctionPlayersPool.value = ArrayList(pool)

            // Trigger AI Bot counter-bid after a delay
            viewModelScope.launch {
                delay(2000)
                considerAiCounterBid(currentItem)
            }
        }
    }

    private fun considerAiCounterBid(item: AuctionItem) {
        val aiTeams = allTeams.value.filter { it.id != userTeamId.value }
        if (aiTeams.isEmpty()) return

        // 55% chance an AI franchise bids if rating is high
        if (Math.random() < 0.55 && item.currentBidLakhs < item.rating * 18) {
            val rival = aiTeams.random()
            val raiseIncrement = if (item.currentBidLakhs >= 1000) 50 else 25
            item.currentBidLakhs += raiseIncrement
            item.highestBidderTeam = rival.name
            item.highestBidderId = rival.id
            _auctionTimer.value = 8
            _auctionPlayersPool.value = ArrayList(_auctionPlayersPool.value)
        }
    }

    fun passAuctionItem() {
        val idx = _currentAuctionIndex.value
        val pool = _auctionPlayersPool.value
        if (idx >= pool.size) return
        val item = pool[idx]

        // Finalize this player
        if (item.highestBidderId != null) {
            item.isSold = true
            if (item.highestBidderId == userTeamId.value) {
                userPurseLakhs.value = maxOf(0, userPurseLakhs.value - item.currentBidLakhs)
                // Add to user squad
                viewModelScope.launch {
                    val bought = PlayerEntity(
                        teamId = userTeamId.value,
                        name = item.name,
                        role = item.role,
                        battingSkill = if (item.role == PlayerRole.BATSMAN) item.rating else item.rating - 15,
                        bowlingSkill = if (item.role == PlayerRole.BOWLER) item.rating else if (item.role == PlayerRole.ALL_ROUNDER) item.rating - 5 else 30,
                        jerseyNumber = (1..99).random(),
                        inPlayingXi = true
                    )
                    repository.insertPlayer(bought)
                    selectTeam(userTeamId.value)
                }
            }
        } else {
            item.isPassed = true
        }

        // Advance to next player
        if (idx + 1 < pool.size) {
            _currentAuctionIndex.value = idx + 1
            _auctionTimer.value = 10
        } else {
            // Auction Completed!
            _isSeasonAuctionActive.value = false
        }
        _auctionPlayersPool.value = ArrayList(pool)
    }

    fun completeAuction() {
        _isSeasonAuctionActive.value = false
    }

    fun advanceToNextSeason() {
        seasonNumber.value += 1
        userPurseLakhs.value += 2000 // New season sponsorship injection: ₹20.00 Cr
        triggerSeasonStartAuction()
    }

    // ==========================================
    // TRAINING SYSTEM
    // ==========================================
    fun trainPlayer(player: PlayerEntity, discipline: String) {
        viewModelScope.launch {
            val updated = when (discipline) {
                "Batting" -> player.copy(battingSkill = minOf(99, player.battingSkill + 1))
                "Bowling" -> player.copy(bowlingSkill = minOf(99, player.bowlingSkill + 1))
                "Fielding" -> player.copy(fieldingSkill = minOf(99, player.fieldingSkill + 1))
                else -> player.copy(
                    battingSkill = minOf(99, player.battingSkill + 1),
                    fieldingSkill = minOf(99, player.fieldingSkill + 1)
                )
            }
            repository.updatePlayer(updated)
            selectTeam(player.teamId)
        }
    }

    fun setFieldPreset(preset: String) {
        _fieldPreset.value = preset
    }

    // ==========================================
    // NEWS & INBOX
    // ==========================================
    val newsList = MutableStateFlow(
        listOf(
            NewsItem("N1", "Season Mega Auction Opens with Fierce Bidding", "AUCTION", "Today", "Franchises prepare their war-chests as top T20 specialists and emerging youth prospects go under the hammer."),
            NewsItem("N2", "Youth Academy Breakthrough: Scouts Discover New Talent", "ACADEMY", "Yesterday", "The franchise youth development wing unveils outstanding prodigies ready to step up into the senior squad."),
            NewsItem("N3", "Championship Race Heats Up: Playoff Scenarios Explained", "LEAGUE", "2 days ago", "With net run rates razor thin, top contenders battle for qualification spots in the final tournament stretch."),
            NewsItem("N4", "Pitch Condition Advisory: Fast Bowlers Expect Extra Bounce", "VENUE", "3 days ago", "Head curator reveals the wicket is prepared with a firm green top, favoring attacking stroke play and fiery seamers.")
        )
    )

    // ==========================================
    // TROPHY CABINET
    // ==========================================
    val trophiesList = MutableStateFlow(
        listOf(
            TrophyItem("T1", "Premier League Championship Cup", "National T20 Cup", "Season 1", true, "Awarded to the ultimate champion of the T20 franchise league."),
            TrophyItem("T2", "Super League Gold Shield", "Super League", null, false, "Awarded for winning the league stage with highest points table finish."),
            TrophyItem("T3", "Orange Cap Honor", "Top Run Scorer", "Season 1", true, "Presented to the most prolific run-getter of the tournament."),
            TrophyItem("T4", "Purple Cap Honor", "Top Wicket Taker", null, false, "Presented to the most lethal bowler with highest wickets."),
            TrophyItem("T5", "Youth Development Shield", "Academy Excellence", "Season 1", true, "Honoring outstanding youth player promotion into professional senior ranks.")
        )
    )

    // ==========================================
    // EXISTING SIMULATION & MATCH METHODS
    // (Preserved exactly as required)
    // ==========================================

    init {
        // Load initial tournament standings
        viewModelScope.launch {
            repository.allTournaments.collect { list ->
                if (list.isNotEmpty()) {
                    loadTournamentStandings(list.first().id)
                }
            }
        }
        viewModelScope.launch {
            repository.allTeams.collect { teams ->
                if (teams.isNotEmpty() && _selectedTeam.value == null) {
                    selectTeam(teams.first().id)
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
                val allBatting = _battingTeamPlayers.value
                val battedIds = (_matchBalls.value.map { it.strikerId } + _matchBalls.value.map { it.nonStrikerId }).toSet() + striker.id
                val nextBatter = allBatting.firstOrNull { it.id !in battedIds && it.id != nextNonStrikerId }
                nextStrikerId = nextBatter?.id
            } else {
                if (ball.runsBat % 2 == 1 || (ball.extraType == ExtraType.WIDE && ball.extraRuns % 2 == 1)) {
                    val temp = nextStrikerId
                    nextStrikerId = nextNonStrikerId
                    nextNonStrikerId = temp
                }
            }

            // End of over handling
            val overCompleted = isLegal && (newBalls % 6 == 0)
            if (overCompleted) {
                val temp = nextStrikerId
                nextStrikerId = nextNonStrikerId
                nextNonStrikerId = temp

                val bowlingXI = _bowlingTeamPlayers.value
                val eligibleBowlers = bowlingXI.filter { it.id != bowler.id }
                if (eligibleBowlers.isNotEmpty()) {
                    val nextB = eligibleBowlers.filter { it.role == PlayerRole.BOWLER || it.role == PlayerRole.ALL_ROUNDER }
                        .randomOrNull() ?: eligibleBowlers.random()
                    nextBowlerId = nextB.id
                }
            }

            val maxBalls = match.oversPerInnings * 6
            val allOut = newWickets >= 10 || nextStrikerId == null

            if (match.currentInnings == 1) {
                if (newBalls >= maxBalls || allOut) {
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
