package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cricketmanager.data.model.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: Long): MatchEntity?

    @Query("SELECT * FROM matches WHERE id = :id")
    fun getMatchByIdFlow(id: Long): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY id ASC")
    fun getMatchesForTournament(tournamentId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status = 'IN_PROGRESS' ORDER BY createdAt DESC LIMIT 1")
    fun getActiveMatch(): Flow<MatchEntity?>

    @Query("SELECT * FROM matches ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMatches(limit: Int = 5): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)
}
