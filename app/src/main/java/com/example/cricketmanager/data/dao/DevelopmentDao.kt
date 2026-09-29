package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cricketmanager.data.model.PlayerDevelopmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DevelopmentDao {
    @Query("SELECT * FROM player_development WHERE playerId = :playerId")
    fun observe(playerId: Long): Flow<PlayerDevelopmentEntity?>

    @Query("SELECT * FROM player_development")
    fun observeAll(): Flow<List<PlayerDevelopmentEntity>>

    @Query("SELECT * FROM player_development WHERE playerId = :playerId")
    suspend fun get(playerId: Long): PlayerDevelopmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlayerDevelopmentEntity)

    @Update
    suspend fun update(entity: PlayerDevelopmentEntity)
}
