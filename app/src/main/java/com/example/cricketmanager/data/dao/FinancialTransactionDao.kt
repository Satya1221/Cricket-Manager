package com.example.cricketmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cricketmanager.data.model.FinancialTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialTransactionDao {
    @Query("SELECT * FROM financial_transactions WHERE teamId = :teamId ORDER BY createdAt DESC")
    fun observeForTeam(teamId: Long): Flow<List<FinancialTransactionEntity>>

    @Insert
    suspend fun insert(transaction: FinancialTransactionEntity): Long
}
