package com.example.cricketmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cricketmanager.data.dao.*
import com.example.cricketmanager.data.model.*

@Database(
    entities = [
        TeamEntity::class, PlayerEntity::class, MatchEntity::class, BallEventEntity::class,
        TournamentEntity::class, TournamentStandingsEntity::class,
        ContractEntity::class, PlayerDevelopmentEntity::class, FinancialTransactionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun ballEventDao(): BallEventDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun contractDao(): ContractDao
    abstract fun developmentDao(): DevelopmentDao
    abstract fun financialTransactionDao(): FinancialTransactionDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS contracts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, playerId INTEGER NOT NULL, teamId INTEGER NOT NULL, salary REAL NOT NULL, yearsRemaining INTEGER NOT NULL, isRetained INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_contracts_playerId ON contracts(playerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_contracts_teamId ON contracts(teamId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS player_development (playerId INTEGER NOT NULL, potential INTEGER NOT NULL, form REAL NOT NULL, morale REAL NOT NULL, fitness REAL NOT NULL, trainingFocus TEXT NOT NULL, PRIMARY KEY(playerId))")
                db.execSQL("CREATE TABLE IF NOT EXISTS financial_transactions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, season INTEGER NOT NULL, teamId INTEGER NOT NULL, amount REAL NOT NULL, type TEXT NOT NULL, description TEXT NOT NULL, createdAt INTEGER NOT NULL)")
            }
        }

        @Volatile private var INSTANCE: CricketDatabase? = null

        fun getDatabase(context: Context): CricketDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                CricketDatabase::class.java,
                "cricket_manager_db"
            ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
        }
    }
}
