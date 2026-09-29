package com.example.cricketmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        TeamEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        BallEventEntity::class,
        TournamentEntity::class,
        TournamentStandingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun ballEventDao(): BallEventDao
    abstract fun tournamentDao(): TournamentDao

    companion object {
        @Volatile
        private var INSTANCE: CricketDatabase? = null

        fun getDatabase(context: Context): CricketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricketDatabase::class.java,
                    "cricket_manager_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
