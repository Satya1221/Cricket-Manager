package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val shortCode: String,
    val city: String,
    val primaryColorHex: String = "#125C44",
    val secondaryColorHex: String = "#D4AF37",
    val homeGround: String = "National Cricket Arena",
    val isCustom: Boolean = false,
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val matchesLost: Int = 0
)
