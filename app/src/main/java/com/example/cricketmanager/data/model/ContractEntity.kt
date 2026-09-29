package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "contracts",
    indices = [Index(value = ["playerId"]), Index(value = ["teamId"])]
)
data class ContractEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerId: Long,
    val teamId: Long,
    val salary: Double,
    val yearsRemaining: Int,
    val isRetained: Boolean
)
