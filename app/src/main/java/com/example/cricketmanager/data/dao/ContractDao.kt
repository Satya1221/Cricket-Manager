package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cricketmanager.data.model.ContractEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContractDao {
    @Query("SELECT * FROM contracts WHERE teamId = :teamId")
    fun getContractsForTeam(teamId: Long): Flow<List<ContractEntity>>

    @Query("SELECT * FROM contracts WHERE playerId = :playerId LIMIT 1")
    suspend fun getContractForPlayer(playerId: Long): ContractEntity?

    @Query("SELECT * FROM contracts")
    fun getAllContracts(): Flow<List<ContractEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contract: ContractEntity): Long

    @Update
    suspend fun update(contract: ContractEntity)

    @Query("DELETE FROM contracts WHERE id = :contractId")
    suspend fun delete(contractId: Long)
}
