package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_transactions")
data class FinancialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val season: Int,
    val teamId: Long,
    val amount: Double,
    val type: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
