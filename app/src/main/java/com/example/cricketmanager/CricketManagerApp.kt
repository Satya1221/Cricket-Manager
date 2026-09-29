package com.example.cricketmanager

import android.app.Application
import com.example.cricketmanager.data.database.CricketDatabase
import com.example.cricketmanager.data.database.DatabaseInitializer
import com.example.cricketmanager.data.repository.CricketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CricketManagerApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { CricketDatabase.getDatabase(this) }
    val repository by lazy {
        CricketRepository(
            teamDao = database.teamDao(),
            playerDao = database.playerDao(),
            matchDao = database.matchDao(),
            ballEventDao = database.ballEventDao(),
            tournamentDao = database.tournamentDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            DatabaseInitializer.populateInitialData(
                teamDao = database.teamDao(),
                playerDao = database.playerDao(),
                matchDao = database.matchDao(),
                tournamentDao = database.tournamentDao()
            )
        }
    }
}
