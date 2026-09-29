package com.example.cricketmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.cricketmanager.ui.components.PremiumBottomNavigation
import com.example.cricketmanager.ui.navigation.CricketNavDestination
import com.example.cricketmanager.ui.screens.*
import com.example.cricketmanager.ui.theme.CricketManagerTheme
import com.example.cricketmanager.ui.theme.CricketNavyBackground
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

    // Secondary sub-screen navigation state
    var activeSubScreen by remember { mutableStateOf<String?>(null) }
    var viewingTeamDetailId by remember { mutableStateOf<Long?>(null) }
    var viewingScorecardMatchId by remember { mutableStateOf<Long?>(null) }
    var showStartMatchModal by remember { mutableStateOf(false) }

    val teams by viewModel.allTeams.collectAsState()

    val isSubScreenOpen = activeSubScreen != null || viewingTeamDetailId != null || viewingScorecardMatchId != null

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CricketNavyBackground),
        containerColor = CricketNavyBackground,
        bottomBar = {
            if (!isSubScreenOpen) {
                PremiumBottomNavigation(
                    selectedRoute = currentTab.route,
                    onTabSelected = { route ->
                        currentTab = when (route) {
                            "home" -> CricketNavDestination.HOME
                            "squad" -> CricketNavDestination.SQUAD
                            "matches" -> CricketNavDestination.MATCHES
                            "youth" -> CricketNavDestination.YOUTH
                            "more" -> CricketNavDestination.MORE
                            else -> CricketNavDestination.HOME
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        when {
            viewingScorecardMatchId != null -> {
                ScorecardScreen(
                    viewModel = viewModel,
                    matchId = viewingScorecardMatchId!!,
                    onNavigateBack = { viewingScorecardMatchId = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            viewingTeamDetailId != null -> {
                TeamDetailScreen(
                    viewModel = viewModel,
                    teamId = viewingTeamDetailId!!,
                    onNavigateBack = { viewingTeamDetailId = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "auction" -> {
                AuctionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "tactics" -> {
                TacticsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "training" -> {
                TrainingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "scouting" -> {
                ScoutingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "league" -> {
                TournamentScreen(
                    viewModel = viewModel,
                    onNavigateToMatch = { matchId ->
                        viewModel.selectMatch(matchId)
                        activeSubScreen = null
                        currentTab = CricketNavDestination.MATCHES
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "finance" -> {
                FinanceScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "trophies" -> {
                TrophiesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "news" -> {
                NewsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "settings" -> {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            activeSubScreen == "match_centre" -> {
                MatchCentreScreen(
                    viewModel = viewModel,
                    onPlayMatch = { team1Id, team2Id, overs, tossWinner, tossDecision ->
                        viewModel.startNewMatch(
                            team1Id = team1Id,
                            team2Id = team2Id,
                            overs = overs,
                            tossWinnerId = tossWinner,
                            tossDecision = tossDecision
                        )
                        activeSubScreen = null
                        currentTab = CricketNavDestination.MATCHES
                    },
                    onNavigateBack = { activeSubScreen = null },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                when (currentTab) {
                    CricketNavDestination.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToMatch = { matchId ->
                                viewModel.selectMatch(matchId)
                                currentTab = CricketNavDestination.MATCHES
                            },
                            onNavigateToMatchCentre = { activeSubScreen = "match_centre" },
                            onNavigateToSquad = { currentTab = CricketNavDestination.SQUAD },
                            onNavigateToTactics = { activeSubScreen = "tactics" },
                            onNavigateToTraining = { activeSubScreen = "training" },
                            onNavigateToScouting = { activeSubScreen = "scouting" },
                            onNavigateToYouth = { currentTab = CricketNavDestination.YOUTH },
                            onNavigateToAuction = { activeSubScreen = "auction" },
                            onNavigateToLeague = { activeSubScreen = "league" },
                            onNavigateToFinance = { activeSubScreen = "finance" },
                            onNavigateToTrophies = { activeSubScreen = "trophies" },
                            onNavigateToNews = { activeSubScreen = "news" },
                            onNavigateToSettings = { activeSubScreen = "settings" },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.SQUAD -> {
                        TeamsScreen(
                            viewModel = viewModel,
                            onTeamSelected = { teamId ->
                                viewModel.selectTeam(teamId)
                                viewingTeamDetailId = teamId
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.MATCHES -> {
                        LiveMatchScreen(
                            viewModel = viewModel,
                            onNavigateToScorecard = { matchId ->
                                viewingScorecardMatchId = matchId
                            },
                            onStartNewMatchRequested = {
                                activeSubScreen = "match_centre"
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.YOUTH -> {
                        YouthAcademyScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                    CricketNavDestination.MORE -> {
                        MoreHubScreen(
                            viewModel = viewModel,
                            onNavigateToTactics = { activeSubScreen = "tactics" },
                            onNavigateToTraining = { activeSubScreen = "training" },
                            onNavigateToScouting = { activeSubScreen = "scouting" },
                            onNavigateToAuction = { activeSubScreen = "auction" },
                            onNavigateToLeague = { activeSubScreen = "league" },
                            onNavigateToFinance = { activeSubScreen = "finance" },
                            onNavigateToTrophies = { activeSubScreen = "trophies" },
                            onNavigateToNews = { activeSubScreen = "news" },
                            onNavigateToSettings = { activeSubScreen = "settings" },
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
                currentTab = CricketNavDestination.MATCHES
            }
        )
    }
}
