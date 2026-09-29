package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_development")
data class PlayerDevelopmentEntity(
    @PrimaryKey val playerId: Long,
    val potential: Int,
    val form: Float,
    val morale: Float,
    val fitness: Float,
    val trainingFocus: String
)
