package com.example.cricketmanager.engine

/**
 * Avoids showing the same "encourage player" action after every match.
 * Interaction becomes available for milestones, poor form, or after a short cooldown.
 */
object PostMatchInteractionPolicy {
    fun shouldOfferEncouragement(
        matchesSinceLastConversation: Int,
        performanceScore: Int,
        isMilestone: Boolean = false
    ): Boolean {
        if (isMilestone) return true
        if (performanceScore <= 40) return true
        if (performanceScore >= 90) return true
        return matchesSinceLastConversation >= 3
    }

    fun availableActions(performanceScore: Int): List<String> = when {
        performanceScore >= 90 -> listOf("Praise performance", "Discuss next target", "Rest player")
        performanceScore <= 40 -> listOf("Encourage player", "Review technique", "Rest player")
        else -> listOf("Review match", "Plan training", "Rest player")
    }
}
