package com.example.cricketmanager.data.model

data class GameDate(
    val day: Int = 15,
    val month: String = "April",
    val year: Int = 2026,
    val dayNumberInSeason: Int = 1
) {
    val formatted: String
        get() = "$day $month $year (Day $dayNumberInSeason)"

    fun nextDay(): GameDate {
        val months = listOf("April", "May", "June", "July", "August", "September", "October", "November", "December", "January", "February", "March")
        val monthIdx = months.indexOf(month)
        val maxDays = when (month) {
            "February" -> 28
            "April", "June", "September", "November" -> 30
            else -> 31
        }
        return if (day < maxDays) {
            copy(day = day + 1, dayNumberInSeason = dayNumberInSeason + 1)
        } else {
            val nextMonthIdx = (monthIdx + 1) % months.size
            val nextYear = if (nextMonthIdx == 9) year + 1 else year
            copy(day = 1, month = months[nextMonthIdx], year = nextYear, dayNumberInSeason = dayNumberInSeason + 1)
        }
    }
}

enum class FacilityType(val displayName: String, val baseCostLakhs: Int, val description: String) {
    STADIUM("Main Arena & Hospitality", 500, "Increases matchday ticket revenue & corporate luxury box sales"),
    TRAINING_COMPLEX("High-Performance Centre", 350, "Accelerates squad attribute growth and minimizes training fatigue"),
    MEDICAL_CENTRE("Sports Science & Rehab Clinic", 300, "Reduces injury recovery days by up to 50% and monitors fitness"),
    YOUTH_ACADEMY("Youth Development Hub", 250, "Attracts high-potential (90+) wonderkids to the annual intake pool"),
    COMMERCIAL_CENTRE("Global Merchandise & Media HQ", 400, "Multiplies annual brand sponsorship and merchandise sales")
}

data class FacilityStatus(
    val type: FacilityType,
    val level: Int = 1,
    val maxLevel: Int = 5
) {
    val upgradeCostLakhs: Int
        get() = type.baseCostLakhs * level

    val upgradeCostFormatted: String
        get() = if (upgradeCostLakhs >= 100) "₹${String.format("%.2f", upgradeCostLakhs / 100.0)} Cr" else "₹${upgradeCostLakhs} L"
}

data class PlayerInjury(
    val playerId: Long,
    val injuryName: String,
    val severity: String,
    var daysRemaining: Int
)

enum class StaffRole(val title: String) {
    HEAD_COACH("Head Coach"),
    BATTING_COACH("Batting Master"),
    BOWLING_COACH("Bowling Specialist"),
    HEAD_PHYSIO("Chief Physiotherapist"),
    CHIEF_SCOUT("Global Talent Scout")
}

data class StaffMember(
    val id: String,
    val name: String,
    val role: StaffRole,
    val rating: Int,
    val specialty: String,
    val salaryLakhsPerYear: Int
)

data class BoardObjective(
    val id: String,
    val title: String,
    val category: String,
    val targetDescription: String,
    val isCompleted: Boolean = false,
    val progressPercent: Int = 0
)

data class SaveGameSlot(
    val slotId: Int,
    val slotTitle: String,
    val teamName: String,
    val seasonNumber: Int,
    val inGameDate: String,
    val purseLakhs: Int,
    val lastSavedTimestamp: String,
    val isOccupied: Boolean = false
) {
    val purseFormatted: String
        get() = if (purseLakhs >= 100) "₹${String.format("%.2f", purseLakhs / 100.0)} Cr" else "₹${purseLakhs} L"
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isUrgent: Boolean = false,
    val type: String = "INFO" // INJURY, SPONSOR, MATCH, FACILITY, BOARD
)
