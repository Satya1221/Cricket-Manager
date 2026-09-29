package com.example.cricketmanager.data.manager

import com.example.cricketmanager.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class ClubManager {

    // 1. Calendar System
    private val _gameDate = MutableStateFlow(GameDate(day = 15, month = "April", year = 2026, dayNumberInSeason = 1))
    val gameDate: StateFlow<GameDate> = _gameDate.asStateFlow()

    // 2. Club Facilities System
    private val _facilities = MutableStateFlow(
        listOf(
            FacilityStatus(FacilityType.STADIUM, level = 2),
            FacilityStatus(FacilityType.TRAINING_COMPLEX, level = 2),
            FacilityStatus(FacilityType.MEDICAL_CENTRE, level = 1),
            FacilityStatus(FacilityType.YOUTH_ACADEMY, level = 2),
            FacilityStatus(FacilityType.COMMERCIAL_CENTRE, level = 1)
        )
    )
    val facilities: StateFlow<List<FacilityStatus>> = _facilities.asStateFlow()

    // 3. Medical & Injury System
    private val _injuries = MutableStateFlow<List<PlayerInjury>>(
        listOf(
            PlayerInjury(playerId = 3L, injuryName = "Hamstring Strain", severity = "Moderate", daysRemaining = 5)
        )
    )
    val injuries: StateFlow<List<PlayerInjury>> = _injuries.asStateFlow()

    // 4. Staff System
    private val _staff = MutableStateFlow(
        listOf(
            StaffMember("ST_1", "Zaheer Khan", StaffRole.HEAD_COACH, 89, "Tactical Bowling Setups", 450),
            StaffMember("ST_2", "Michael Hussey", StaffRole.BATTING_COACH, 91, "Power Hitting & Composure", 380),
            StaffMember("ST_3", "Shane Bond", StaffRole.BOWLING_COACH, 88, "Express Pace & Yorkers", 350),
            StaffMember("ST_4", "Dr. Patrick Farhart", StaffRole.HEAD_PHYSIO, 94, "Rapid Muscle Rehabilitation", 300),
            StaffMember("ST_5", "John Wright", StaffRole.CHIEF_SCOUT, 87, "Domestic Wonderkid Talent", 250)
        )
    )
    val staff: StateFlow<List<StaffMember>> = _staff.asStateFlow()

    // 5. Board & Fan Systems
    val boardConfidence = MutableStateFlow(84) // 84%
    val fanConfidence = MutableStateFlow(92)   // 92%

    private val _boardObjectives = MutableStateFlow(
        listOf(
            BoardObjective("BO_1", "Reach Championship Playoffs", "LEAGUE", "Finish in the Top 4 of the points table", false, 60),
            BoardObjective("BO_2", "Promote Youth Wonderkid", "YOUTH", "Promote at least 1 academy graduate into Playing XI", true, 100),
            BoardObjective("BO_3", "Maintain Financial Stability", "FINANCIAL", "Ensure remaining purse is above ₹30.00 Cr", true, 80),
            BoardObjective("BO_4", "Stadium Hospitality Upgrade", "COMMERCIAL", "Upgrade Main Arena to Level 3", false, 50)
        )
    )
    val boardObjectives: StateFlow<List<BoardObjective>> = _boardObjectives.asStateFlow()

    // 6. Save & Load System (Multiple Slots)
    private val _saveSlots = MutableStateFlow(
        listOf(
            SaveGameSlot(1, "Career Slot 1 (Auto-Save)", "Mumbai Strikers", 1, "15 April 2026", 6850, "Today, 18:45", true),
            SaveGameSlot(2, "Career Slot 2 (Manual)", "Chennai Super Kings", 1, "10 April 2026", 7200, "Yesterday, 21:10", true),
            SaveGameSlot(3, "Career Slot 3 (Empty)", "-", 1, "-", 0, "No Save Data", false)
        )
    )
    val saveSlots: StateFlow<List<SaveGameSlot>> = _saveSlots.asStateFlow()

    // 7. Notification Feed
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("NF_1", "Matchday Approaching", "Upcoming fixture against Chennai Super Kings scheduled at home arena.", "Today", false, "MATCH"),
            NotificationItem("NF_2", "Medical Rehab Update", "Medical clinic reports hamstring strain recovery progressing on schedule.", "Yesterday", false, "INJURY"),
            NotificationItem("NF_3", "Mega Auction Notice", "Board confirmed auction purse budget allocated for current season.", "2 days ago", false, "BOARD")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Methods
    fun advanceDay(currentPurseLakhs: Int, onPurseUpdated: (Int) -> Unit) {
        val nextDate = _gameDate.value.nextDay()
        _gameDate.value = nextDate

        // Medical recovery tick (medical center level accelerates healing)
        val medicalLevel = _facilities.value.find { it.type == FacilityType.MEDICAL_CENTRE }?.level ?: 1
        val recoveryPoints = if (medicalLevel >= 3) 2 else 1

        val updatedInjuries = mutableListOf<PlayerInjury>()
        _injuries.value.forEach { injury ->
            val remaining = injury.daysRemaining - recoveryPoints
            if (remaining > 0) {
                updatedInjuries.add(injury.copy(daysRemaining = remaining))
            } else {
                // Recovered! Push notification
                addNotification(
                    title = "Player Recovered",
                    message = "Player has completed medical rehabilitation and is now fully fit for match selection!",
                    isUrgent = false,
                    type = "INJURY"
                )
            }
        }
        _injuries.value = updatedInjuries

        // Matchday/Daily revenue injection from Commercial & Stadium facilities
        val stadiumLevel = _facilities.value.find { it.type == FacilityType.STADIUM }?.level ?: 1
        val commercialLevel = _facilities.value.find { it.type == FacilityType.COMMERCIAL_CENTRE }?.level ?: 1
        val dailyRevenue = (stadiumLevel * 4) + (commercialLevel * 5)
        onPurseUpdated(currentPurseLakhs + dailyRevenue)
    }

    fun upgradeFacility(type: FacilityType, currentPurseLakhs: Int, onPurseDeducted: (Int) -> Unit): Boolean {
        val currentList = _facilities.value
        val item = currentList.find { it.type == type } ?: return false
        if (item.level >= item.maxLevel) return false

        val cost = item.upgradeCostLakhs
        if (currentPurseLakhs >= cost) {
            onPurseDeducted(currentPurseLakhs - cost)
            _facilities.value = currentList.map {
                if (it.type == type) it.copy(level = it.level + 1) else it
            }
            addNotification(
                title = "Facility Upgrade Complete",
                message = "${type.displayName} upgraded to Level ${item.level + 1}!",
                isUrgent = false,
                type = "FACILITY"
            )
            return true
        }
        return false
    }

    fun saveGame(slotId: Int, teamName: String, season: Int, purseLakhs: Int) {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        val timestamp = sdf.format(Date())
        _saveSlots.value = _saveSlots.value.map { slot ->
            if (slot.slotId == slotId) {
                slot.copy(
                    teamName = teamName,
                    seasonNumber = season,
                    inGameDate = _gameDate.value.formatted,
                    purseLakhs = purseLakhs,
                    lastSavedTimestamp = timestamp,
                    isOccupied = true
                )
            } else slot
        }
        addNotification(
            title = "Game Saved",
            message = "Franchise career successfully saved to Slot $slotId.",
            isUrgent = false,
            type = "INFO"
        )
    }

    fun loadGame(slotId: Int, onLoaded: (season: Int, purse: Int) -> Unit) {
        val slot = _saveSlots.value.find { it.slotId == slotId && it.isOccupied } ?: return
        onLoaded(slot.seasonNumber, slot.purseLakhs)
        addNotification(
            title = "Game Loaded",
            message = "Resumed career from Slot $slotId (${slot.teamName}).",
            isUrgent = false,
            type = "INFO"
        )
    }

    fun addNotification(title: String, message: String, isUrgent: Boolean, type: String) {
        val newN = NotificationItem(
            id = "NF_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timestamp = "Just now",
            isUrgent = isUrgent,
            type = type
        )
        _notifications.value = listOf(newN) + _notifications.value.take(15)
    }
}
