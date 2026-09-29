package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cricketmanager.data.model.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY inPlayingXi DESC, battingOrder ASC, name ASC")
    fun getPlayersForTeam(teamId: Long): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE teamId = :teamId AND inPlayingXi = 1 ORDER BY battingOrder ASC")
    suspend fun getPlayingXi(teamId: Long): List<PlayerEntity>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE id IN (:ids)")
    suspend fun getPlayersByIds(ids: List<Long>): List<PlayerEntity>

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>): List<Long>

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayers(players: List<PlayerEntity>)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE runsScored > 0 ORDER BY runsScored DESC LIMIT :limit")
    fun getTopRunScorers(limit: Int = 10): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE wicketsTaken > 0 ORDER BY wicketsTaken DESC LIMIT :limit")
    fun getTopWicketTakers(limit: Int = 10): Flow<List<PlayerEntity>>
}
