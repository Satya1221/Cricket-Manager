package com.example.cricketmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExtraType {
    NONE,
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE
}

enum class WicketType {
    NONE,
    BOWLED,
    CAUGHT,
    LBW,
    RUN_OUT,
    STUMPED,
    HIT_WICKET
}

@Entity(tableName = "ball_events")
data class BallEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matchId: Long,
    val inningsNumber: Int,
    val overNumber: Int,
    val ballNumberInOver: Int,
    val bowlerId: Long,
    val strikerId: Long,
    val nonStrikerId: Long,
    val runsBat: Int = 0,
    val extraType: ExtraType = ExtraType.NONE,
    val extraRuns: Int = 0,
    val isWicket: Boolean = false,
    val wicketType: WicketType = WicketType.NONE,
    val dismissedPlayerId: Long? = null,
    val commentary: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    val totalRuns: Int
        get() = runsBat + extraRuns

    val isLegalBall: Boolean
        get() = extraType != ExtraType.WIDE && extraType != ExtraType.NO_BALL

    val shortLabel: String
        get() = when {
            isWicket -> "W"
            extraType == ExtraType.WIDE -> if (extraRuns > 1) "${extraRuns}wd" else "wd"
            extraType == ExtraType.NO_BALL -> if (runsBat > 0) "${runsBat}nb" else "nb"
            extraType == ExtraType.BYE -> "${extraRuns}b"
            extraType == ExtraType.LEG_BYE -> "${extraRuns}lb"
            runsBat == 4 -> "4"
            runsBat == 6 -> "6"
            runsBat == 0 -> "•"
            else -> "$runsBat"
        }
}
