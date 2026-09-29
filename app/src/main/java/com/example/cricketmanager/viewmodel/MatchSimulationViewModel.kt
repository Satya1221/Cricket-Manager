package com.example.cricketmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.repository.CricketRepository
import com.example.cricketmanager.engine.BattingMindset
import com.example.cricketmanager.engine.BowlingPlan
import com.example.cricketmanager.engine.MatchSimulationEngine
import com.example.cricketmanager.engine.PitchType
import com.example.cricketmanager.engine.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI-agnostic state holder for live match simulation using unidirectional state flow. */
data class MatchUiState(
    val runs: Int = 0,
    val wickets: Int = 0,
    val balls: Int = 0,
    val currentBatter: PlayerEntity? = null,
    val nonStriker: PlayerEntity? = null,
    val currentBowler: PlayerEntity? = null,
    val pitchCondition: PitchType = PitchType.BALANCED,
    val weather: WeatherCondition = WeatherCondition.CLEAR,
    val selectedBattingMindset: BattingMindset = BattingMindset.BALANCED,
    val selectedBowlingPlan: BowlingPlan = BowlingPlan.BALANCED,
    val lastOutcome: BallEventEntity? = null,
    val isMatchOver: Boolean = false
) {
    fun applyOutcome(outcome: BallEventEntity): MatchUiState {
        val newRuns = runs + outcome.runsBat + outcome.extraRuns
        val newWickets = wickets + if (outcome.isWicket) 1 else 0
        val legalBall = outcome.extraType.name == "NONE"
        val newBalls = balls + if (legalBall) 1 else 0
        return copy(
            runs = newRuns,
            wickets = newWickets,
            balls = newBalls,
            lastOutcome = outcome,
            isMatchOver = newWickets >= 10
        )
    }
}

class MatchSimulationViewModel(
    private val repository: CricketRepository,
    private val matchId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    fun initialise(striker: PlayerEntity, nonStriker: PlayerEntity, bowler: PlayerEntity) {
        _uiState.value = _uiState.value.copy(
            currentBatter = striker,
            nonStriker = nonStriker,
            currentBowler = bowler
        )
    }

    fun setConditions(pitch: PitchType, weather: WeatherCondition) {
        _uiState.value = _uiState.value.copy(pitchCondition = pitch, weather = weather)
    }

    fun playNextBall(battingMindset: BattingMindset, bowlingPlan: BowlingPlan) {
        val current = _uiState.value
        val striker = current.currentBatter ?: return
        val nonStriker = current.nonStriker ?: return
        val bowler = current.currentBowler ?: return

        viewModelScope.launch {
            val outcome = MatchSimulationEngine.simulateBall(
                matchId = matchId,
                inningsNumber = 1,
                overNumber = current.balls / 6,
                ballNumberInOver = (current.balls % 6) + 1,
                striker = striker,
                nonStriker = nonStriker,
                bowler = bowler,
                mindset = battingMindset,
                bowlingPlan = bowlingPlan
            )
            val updated = current.applyOutcome(outcome).copy(
                selectedBattingMindset = battingMindset,
                selectedBowlingPlan = bowlingPlan
            )
            _uiState.value = updated
            repository.recordBallEvent(outcome)
        }
    }
}
