package com.example.cricketmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.cricketmanager.data.model.TossDecision
import com.example.cricketmanager.ui.navigation.CricketNavDestination
import com.example.cricketmanager.ui.screens.HomeScreen
import com.example.cricketmanager.ui.screens.LiveMatchScreen
import com.example.cricketmanager.ui.screens.NewMatchDialog
import com.example.cricketmanager.ui.screens.ScorecardScreen
import com.example.cricketmanager.ui.screens.StatsScreen
import com.example.cricketmanager.ui.screens.TeamDetailScreen
import com.example.cricketmanager.ui.screens.TeamsScreen
import com.example.cricketmanager.ui.screens.TournamentScreen
import com.example.cricketmanager.ui.theme.CricketGold
import com.example.cricketmanager.ui.theme.CricketGreenDark
import com.example.cricketmanager.ui.theme.CricketGreenPrimary
import com.example.cricketmanager.ui.theme.CricketManagerTheme
import com.example.cricketmanager.viewmodel.CricketViewModel
import com.example.cricketmanager.viewmodel.CricketViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: CricketViewModel by viewModels {
        val app = application as CricketManagerApp
        CricketViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CricketManagerTheme {
                CricketManagerAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CricketManagerAppContent(viewModel: CricketViewModel) {
    var currentTab by remember { mutableStateOf(CricketNavDestination.HOME) }
    var viewingTeamDetailId by remember { mutableStateOf<Long?>(null) }
    var viewingScorecardMatchId by remember { mutableStateOf<Long?>(null) }
    var showStartMatchModal by remember { mutableStateOf(false) }

    val teams by viewModel.allTeams.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (viewingTeamDetailId == null && viewingScorecardMatchId == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = CricketGreenPrimary
                ) {
                    CricketNavDestination.entries.forEach { destination ->
                        val isSelected = currentTab == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = CricketGreenPrimary,
                                indicatorColor = CricketGreenPrimary
                            ),
                            modifier = Modifier.testTag("nav_tab_${destination.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when {
            viewingScorecardMatchId != null -> {
                ScorecardScreen(
                    viewModel = viewModel,
                    matchId = viewingScorecardMatchId!!,
                    onNavigateBack = { viewingScorecardMatchId = null }
                )
            }
            viewingTeamDetailId != null -> {
                TeamDetailScreen(
                    viewModel = viewModel,
                    teamId = viewingTeamDetailId!!,
                    onNavigateBack = { viewingTeamDetailId = null }
                )
            }
            else -> {
                when (currentTab) {
                    CricketNavDestination.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToMatch = { matchId ->
                                viewModel.selectMatch(matchId)
                                currentTab = CricketNavDestination.LIVE_MATCH
                            },
                            onNavigateToTeams = { currentTab = CricketNavDestination.TEAMS },
                            onNavigateToTournaments = { currentTab = CricketNavDestination.TOURNAMENTS },
                            onNavigateToStats = { currentTab = CricketNavDestination.STATS },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.LIVE_MATCH -> {
                        LiveMatchScreen(
                            viewModel = viewModel,
                            onNavigateToScorecard = { matchId ->
                                viewingScorecardMatchId = matchId
                            },
                            onStartNewMatchRequested = {
                                showStartMatchModal = true
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.TEAMS -> {
                        TeamsScreen(
                            viewModel = viewModel,
                            onTeamSelected = { teamId ->
                                viewModel.selectTeam(teamId)
                                viewingTeamDetailId = teamId
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.TOURNAMENTS -> {
                        TournamentScreen(
                            viewModel = viewModel,
                            onNavigateToMatch = { matchId ->
                                viewModel.selectMatch(matchId)
                                currentTab = CricketNavDestination.LIVE_MATCH
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.STATS -> {
                        StatsScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    if (showStartMatchModal && teams.size >= 2) {
        NewMatchDialog(
            teams = teams,
            onDismiss = { showStartMatchModal = false },
            onConfirm = { team1Id, team2Id, overs, tossWinner, tossDecision ->
                showStartMatchModal = false
                viewModel.startNewMatch(
                    team1Id = team1Id,
                    team2Id = team2Id,
                    overs = overs,
                    tossWinnerId = tossWinner,
                    tossDecision = tossDecision
                )
                currentTab = CricketNavDestination.LIVE_MATCH
            }
        )
    }
}
