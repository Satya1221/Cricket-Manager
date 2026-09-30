package com.example.cricketmanager.engine

/**
 * World cricket competition catalogue used by the career scheduler.
 * Names are competition names only; player/team rosters remain user/custom-roster data.
 */
enum class CompetitionFormat { T20, ODI, TEST, TEST_SERIES }

data class CompetitionDefinition(
    val id: String,
    val name: String,
    val shortName: String,
    val format: CompetitionFormat,
    val matchesPerSeason: Int,
    val international: Boolean = false,
    val activeByDefault: Boolean = true
)

data class ScheduledFixture(
    val competitionId: String,
    val dayNumber: Int,
    val format: CompetitionFormat,
    val label: String
)

object CompetitionCatalog {
    val IPL = CompetitionDefinition("IPL", "Indian Premier League", "IPL", CompetitionFormat.T20, 74)
    val CPL = CompetitionDefinition("CPL", "Caribbean Premier League", "CPL", CompetitionFormat.T20, 34)
    val BBL = CompetitionDefinition("BBL", "Big Bash League", "BBL", CompetitionFormat.T20, 44)
    val PSL = CompetitionDefinition("PSL", "Pakistan Super League", "PSL", CompetitionFormat.T20, 34)
    val SA20 = CompetitionDefinition("SA20", "SA20", "SA20", CompetitionFormat.T20, 34)
    val MLC = CompetitionDefinition("MLC", "Major League Cricket", "MLC", CompetitionFormat.T20, 34)
    val HUNDRED = CompetitionDefinition("HUNDRED", "The Hundred", "Hundred", CompetitionFormat.T20, 34)

    val TEST_CHAMPIONSHIP = CompetitionDefinition(
        "WTC",
        "World Test Championship",
        "WTC",
        CompetitionFormat.TEST_SERIES,
        69,
        international = true
    )
    val INTERNATIONAL_TESTS = CompetitionDefinition(
        "INT_TEST",
        "International Test Cricket",
        "Tests",
        CompetitionFormat.TEST,
        40,
        international = true
    )
    val INTERNATIONAL_ODIS = CompetitionDefinition(
        "INT_ODI",
        "International ODI Cricket",
        "ODIs",
        CompetitionFormat.ODI,
        80,
        international = true
    )

    fun all(): List<CompetitionDefinition> = listOf(
        IPL, CPL, BBL, PSL, SA20, MLC, HUNDRED,
        TEST_CHAMPIONSHIP, INTERNATIONAL_TESTS, INTERNATIONAL_ODIS
    )

    /**
     * Creates a varied calendar instead of concentrating the career on one league.
     * The scheduler deliberately leaves gaps between fixtures for training, travel,
     * recovery, press events and transfers.
     */
    fun generateWorldSchedule(seasonLengthDays: Int = 300): List<ScheduledFixture> {
        require(seasonLengthDays >= 30) { "Season must contain at least 30 days" }

        val fixtures = mutableListOf<ScheduledFixture>()
        var day = 1
        val blocks = listOf(
            Triple(IPL, 12, "League block"),
            Triple(INTERNATIONAL_TESTS, 14, "Test series window"),
            Triple(CPL, 10, "CPL block"),
            Triple(INTERNATIONAL_ODIS, 12, "ODI window"),
            Triple(BBL, 10, "BBL block"),
            Triple(PSL, 10, "PSL block"),
            Triple(TEST_CHAMPIONSHIP, 14, "WTC series window"),
            Triple(SA20, 10, "SA20 block"),
            Triple(MLC, 8, "MLC block"),
            Triple(HUNDRED, 8, "Hundred block")
        )

        var blockIndex = 0
        while (day <= seasonLengthDays) {
            val (competition, spacing, label) = blocks[blockIndex % blocks.size]
            val fixtureCount = when (competition.format) {
                CompetitionFormat.TEST, CompetitionFormat.TEST_SERIES -> 3
                CompetitionFormat.ODI -> 4
                CompetitionFormat.T20 -> 5
            }
            repeat(fixtureCount) { offset ->
                val fixtureDay = day + offset * 2
                if (fixtureDay <= seasonLengthDays) {
                    fixtures += ScheduledFixture(competition.id, fixtureDay, competition.format, label)
                }
            }
            day += spacing
            blockIndex++
        }
        return fixtures.sortedBy { it.dayNumber }
    }
}
