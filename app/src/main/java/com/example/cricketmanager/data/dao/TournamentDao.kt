package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cricketmanager.data.model.TournamentEntity
import com.example.cricketmanager.data.model.TournamentStandingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    suspend fun getTournamentById(id: Long): TournamentEntity?

    @Query("SELECT * FROM tournament_standings WHERE tournamentId = :tournamentId ORDER BY points DESC, netRunRate DESC")
    fun getStandingsForTournament(tournamentId: Long): Flow<List<TournamentStandingsEntity>>

    @Query("SELECT * FROM tournament_standings WHERE tournamentId = :tournamentId AND teamId = :teamId")
    suspend fun getTeamStanding(tournamentId: Long, teamId: Long): TournamentStandingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandings(standings: List<TournamentStandingsEntity>): List<Long>

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    @Update
    suspend fun updateStanding(standing: TournamentStandingsEntity)
}
