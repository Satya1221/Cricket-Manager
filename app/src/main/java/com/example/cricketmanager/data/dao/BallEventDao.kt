package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cricketmanager.data.model.BallEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BallEventDao {
    @Query("SELECT * FROM ball_events WHERE matchId = :matchId ORDER BY id ASC")
    fun getBallsForMatch(matchId: Long): Flow<List<BallEventEntity>>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId AND inningsNumber = :innings ORDER BY id ASC")
    fun getBallsForInnings(matchId: Long, innings: Int): Flow<List<BallEventEntity>>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId AND inningsNumber = :innings ORDER BY id ASC")
    suspend fun getBallsForInningsList(matchId: Long, innings: Int): List<BallEventEntity>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId ORDER BY id DESC LIMIT :limit")
    fun getRecentBalls(matchId: Long, limit: Int = 12): Flow<List<BallEventEntity>>

    @Query("SELECT * FROM ball_events WHERE matchId = :matchId ORDER BY id DESC LIMIT 1")
    suspend fun getLastBall(matchId: Long): BallEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBall(ball: BallEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalls(balls: List<BallEventEntity>): List<Long>

    @Query("DELETE FROM ball_events WHERE id = (SELECT id FROM ball_events WHERE matchId = :matchId ORDER BY id DESC LIMIT 1)")
    suspend fun deleteLastBall(matchId: Long): Int

    @Query("DELETE FROM ball_events WHERE matchId = :matchId")
    suspend fun deleteAllBallsForMatch(matchId: Long)
}
