package com.example.cricketmanager.engine

/**
 * Keeps a field plan attached to the batting pair rather than resetting it whenever
 * strike rotates. A manager can explicitly replace the preset at any time.
 */
data class FieldPlan(
    val preset: String,
    val strikerId: Long,
    val nonStrikerId: Long,
    val lastChangedBall: Int
) {
    val battingPairKey: Set<Long>
        get() = setOf(strikerId, nonStrikerId)
}

object FieldPlanEngine {
    fun create(preset: String, strikerId: Long, nonStrikerId: Long, currentBall: Int): FieldPlan =
        FieldPlan(preset, strikerId, nonStrikerId, currentBall)

    /**
     * Odd runs rotate strike, but the same two batters remain the active pair.
     * Therefore the existing field is preserved. A wicket/new batter is the
     * meaningful automatic trigger for rebuilding the field.
     */
    fun preserveForBall(
        current: FieldPlan,
        strikerId: Long,
        nonStrikerId: Long,
        currentBall: Int,
        wicketFell: Boolean = false,
        explicitPreset: String? = null
    ): FieldPlan {
        val pairChanged = current.battingPairKey != setOf(strikerId, nonStrikerId)
        val presetChanged = explicitPreset != null && explicitPreset != current.preset
        return if (pairChanged || wicketFell || presetChanged) {
            FieldPlan(explicitPreset ?: current.preset, strikerId, nonStrikerId, currentBall)
        } else {
            current.copy(strikerId = strikerId, nonStrikerId = nonStrikerId)
        }
    }

    fun shouldPromptForFieldChange(current: FieldPlan, strikerId: Long, nonStrikerId: Long): Boolean =
        current.battingPairKey != setOf(strikerId, nonStrikerId)
}
