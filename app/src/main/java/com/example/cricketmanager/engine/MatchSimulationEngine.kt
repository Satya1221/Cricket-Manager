package com.example.cricketmanager.engine

import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.data.model.ExtraType
import com.example.cricketmanager.data.model.PlayerEntity
import com.example.cricketmanager.data.model.WicketType
import kotlin.random.Random

enum class PitchType { GREEN, DRY, FLAT, BALANCED }
enum class WeatherCondition { CLEAR, OVERCAST, DEW, RAINY }

data class OutcomeProbabilities(
    val zero: Double,
    val one: Double,
    val two: Double,
    val three: Double,
    val four: Double,
    val six: Double,
    val wicket: Double
)

enum class BattingMindset(val displayName: String, val description: String) {
    DEFENSIVE("Defensive", "Protect wickets, take singles, low risk"),
    BALANCED("Balanced", "Rotate strike, punish bad balls"),
    AGGRESSIVE("Aggressive", "Look for boundaries, high intent"),
    BLITZ("Blitz", "Maximum power hitting, all out attack")
}

enum class BowlingPlan(val displayName: String, val description: String) {
    BALANCED("Standard Length", "Good length around off-stump"),
    ATTACK_STUMPS("Attack Stumps", "Pitched up hunting for Bowled & LBW"),
    YORKER_BLITZ("Yorkers & Slower", "Death over specialty to stop boundaries"),
    SHORT_BOUNCERS("Bouncers & Short", "Aggressive bowling tempting pull shots"),
    DEFENSIVE_WIDE("Wide Outside Off", "Pack the offside boundary, bowl wide line")
}

object MatchSimulationEngine {

    fun outcomeProbabilities(
        striker: PlayerEntity,
        bowler: PlayerEntity,
        mindset: BattingMindset,
        bowlingPlan: BowlingPlan,
        pitch: PitchType = PitchType.BALANCED,
        weather: WeatherCondition = WeatherCondition.CLEAR,
        requiredRunRate: Double = 8.0,
        ballsRemaining: Int = 120,
        wicketsLost: Int = 0
    ): OutcomeProbabilities {
        val skillDiff = ((striker.battingSkill - bowler.bowlingSkill) / 100.0).coerceIn(-0.65, 0.65)
        val pressure = when {
            requiredRunRate >= 13.0 -> 1.20
            requiredRunRate >= 10.0 -> 1.10
            requiredRunRate <= 5.0 -> 0.92
            else -> 1.0
        }
        val urgency = if (ballsRemaining <= 24 && requiredRunRate >= 10.0) 1.08 else 1.0
        var wicket = when (mindset) {
            BattingMindset.DEFENSIVE -> 0.030
            BattingMindset.BALANCED -> 0.052
            BattingMindset.AGGRESSIVE -> 0.080
            BattingMindset.BLITZ -> 0.120
        }
        wicket *= (1.0 - skillDiff * 0.45)
        wicket *= pressure
        if (wicketsLost >= 7) wicket *= 1.15
        if (bowlingPlan == BowlingPlan.ATTACK_STUMPS) wicket *= 1.10
        if (bowlingPlan == BowlingPlan.SHORT_BOUNCERS) wicket *= 1.08
        if (pitch == PitchType.GREEN) wicket *= 1.07
        if (weather == WeatherCondition.OVERCAST) wicket *= 1.05
        wicket = wicket.coerceIn(0.015, 0.22)

        var six = (0.032 + skillDiff * 0.020) * pressure * urgency
        var four = (0.125 + skillDiff * 0.030) * pressure
        var three = 0.022
        var two = 0.110
        var one = 0.355

        when (mindset) {
            BattingMindset.DEFENSIVE -> { six *= 0.30; four *= 0.62; one *= 1.12 }
            BattingMindset.AGGRESSIVE -> { six *= 1.65; four *= 1.28; one *= 0.96 }
            BattingMindset.BLITZ -> { six *= 2.20; four *= 1.55; one *= 0.82 }
            BattingMindset.BALANCED -> Unit
        }
        if (pitch == PitchType.FLAT) { six *= 1.10; four *= 1.08 }
        if (pitch == PitchType.DRY && bowler.bowlingStyle.name.contains("SPIN")) { six *= 0.90; four *= 0.92 }
        if (weather == WeatherCondition.DEW) { six *= 1.06; four *= 1.04 }

        val usable = (1.0 - wicket).coerceAtLeast(0.000001)
        val raw = six + four + three + two + one
        val scale = usable / raw

        return OutcomeProbabilities(
            zero = (usable - raw * scale).coerceAtLeast(0.0),
            one = one * scale,
            two = two * scale,
            three = three * scale,
            four = four * scale,
            six = six * scale,
            wicket = wicket
        )
    }

    fun simulateBall(
        matchId: Long,
        inningsNumber: Int,
        overNumber: Int,
        ballNumberInOver: Int,
        striker: PlayerEntity,
        nonStriker: PlayerEntity,
        bowler: PlayerEntity,
        mindset: BattingMindset = BattingMindset.BALANCED,
        bowlingPlan: BowlingPlan = BowlingPlan.BALANCED
    ): BallEventEntity {
        val rand = Random.nextDouble()

        // 1. Extra check (Wides / No Balls)
        val wideChance = when (bowlingPlan) {
            BowlingPlan.DEFENSIVE_WIDE -> 0.08
            BowlingPlan.YORKER_BLITZ -> 0.05
            else -> 0.03
        }
        val noBallChance = 0.015

        if (rand < wideChance) {
            return BallEventEntity(
                matchId = matchId,
                inningsNumber = inningsNumber,
                overNumber = overNumber,
                ballNumberInOver = ballNumberInOver,
                bowlerId = bowler.id,
                strikerId = striker.id,
                nonStrikerId = nonStriker.id,
                runsBat = 0,
                extraType = ExtraType.WIDE,
                extraRuns = 1,
                isWicket = false,
                commentary = "${bowler.name} strays wide outside the tramline. Called a WIDE by the umpire."
            )
        }

        if (rand < wideChance + noBallChance) {
            return BallEventEntity(
                matchId = matchId,
                inningsNumber = inningsNumber,
                overNumber = overNumber,
                ballNumberInOver = ballNumberInOver,
                bowlerId = bowler.id,
                strikerId = striker.id,
                nonStrikerId = nonStriker.id,
                runsBat = 1,
                extraType = ExtraType.NO_BALL,
                extraRuns = 1,
                isWicket = false,
                commentary = "NO BALL! ${bowler.name} oversteps the bowling crease. Free hit signaled!"
            )
        }

        // 2. Skill differential
        val skillDiff = (striker.battingSkill - bowler.bowlingSkill).toDouble() / 100.0 // -0.5 to +0.5

        // Wicket Probability calculation
        var wicketBaseChance = when (mindset) {
            BattingMindset.DEFENSIVE -> 0.03
            BattingMindset.BALANCED -> 0.06
            BattingMindset.AGGRESSIVE -> 0.11
            BattingMindset.BLITZ -> 0.18
        }
        if (bowlingPlan == BowlingPlan.ATTACK_STUMPS) wicketBaseChance += 0.02
        if (bowlingPlan == BowlingPlan.SHORT_BOUNCERS) wicketBaseChance += 0.015
        wicketBaseChance -= (skillDiff * 0.04)
        wicketBaseChance = wicketBaseChance.coerceIn(0.02, 0.25)

        val roll = Random.nextDouble()

        if (roll < wicketBaseChance) {
            // Wicket fell!
            val wicketType = determineWicketType(bowlingPlan)
            val commentary = generateWicketCommentary(striker.name, bowler.name, wicketType)
            return BallEventEntity(
                matchId = matchId,
                inningsNumber = inningsNumber,
                overNumber = overNumber,
                ballNumberInOver = ballNumberInOver,
                bowlerId = bowler.id,
                strikerId = striker.id,
                nonStrikerId = nonStriker.id,
                runsBat = 0,
                extraType = ExtraType.NONE,
                extraRuns = 0,
                isWicket = true,
                wicketType = wicketType,
                dismissedPlayerId = striker.id,
                commentary = commentary
            )
        }

        // 3. Runs calculation
        var sixChance = 0.04 + (skillDiff * 0.03)
        var fourChance = 0.13 + (skillDiff * 0.05)
        var threeChance = 0.02
        var twoChance = 0.12
        var singleChance = 0.35

        when (mindset) {
            BattingMindset.DEFENSIVE -> {
                sixChance *= 0.2
                fourChance *= 0.5
                singleChance += 0.15
            }
            BattingMindset.BALANCED -> {
                // baseline
            }
            BattingMindset.AGGRESSIVE -> {
                sixChance *= 1.8
                fourChance *= 1.4
            }
            BattingMindset.BLITZ -> {
                sixChance *= 2.6
                fourChance *= 1.7
            }
        }

        val runsRoll = Random.nextDouble()
        val (runs, commentary) = when {
            runsRoll < sixChance -> {
                val shots = listOf(
                    "MASSIVE! ${striker.name} dances down the pitch and launches ${bowler.name} deep into the stands for SIX!",
                    "BOOM! Picked up effortlessly off the pads and dispatched over deep mid-wicket for a huge SIX!",
                    "Maximum! ${striker.name} clears the front leg and smokes it straight back over the bowler's head for SIX!"
                )
                6 to shots.random()
            }
            runsRoll < sixChance + fourChance -> {
                val shots = listOf(
                    "FOUR! Crisp cover drive by ${striker.name}, finds the gap and beats the sweeper to the rope!",
                    "CRACKING SHOT! Short and punished, ${striker.name} pulls it fiercely through square leg for FOUR!",
                    "FOUR RUNS! Steered cleverly through the backward point region with superb timing by ${striker.name}!",
                    "Edged and past the slips! It races away to the third man boundary for FOUR!"
                )
                4 to shots.random()
            }
            runsRoll < sixChance + fourChance + threeChance -> {
                3 to "Superb running between the wickets! Pushed into the deep pocket, ${striker.name} and ${nonStriker.name} push hard for THREE!"
            }
            runsRoll < sixChance + fourChance + threeChance + twoChance -> {
                val shots = listOf(
                    "Tucked off the hips into deep square leg, good call and they sprint back for a comfortable TWO.",
                    "Driven into the gap in the covers, they scamper back for a brace of runs."
                )
                2 to shots.random()
            }
            runsRoll < sixChance + fourChance + threeChance + twoChance + singleChance -> {
                val shots = listOf(
                    "Pushed into mid-on and calls for a sharp single. Good rotation of strike.",
                    "Dabbed softly towards third man, gentle nudge for ONE.",
                    "Full on off stump, knocked down to long-on for a single."
                )
                1 to shots.random()
            }
            else -> {
                val dots = listOf(
                    "Good length ball outside off, ${striker.name} shoulders arms. No run.",
                    "Beaten! Beautiful delivery from ${bowler.name}, seaming away past the outside edge.",
                    "Solid defensive block back to the bowler. Dot ball.",
                    "Pitched up, driven firmly but straight to extra cover. No run taken."
                )
                0 to dots.random()
            }
        }

        return BallEventEntity(
            matchId = matchId,
            inningsNumber = inningsNumber,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            bowlerId = bowler.id,
            strikerId = striker.id,
            nonStrikerId = nonStriker.id,
            runsBat = runs,
            extraType = ExtraType.NONE,
            extraRuns = 0,
            isWicket = false,
            commentary = commentary
        )
    }

    private fun determineWicketType(bowlingPlan: BowlingPlan): WicketType {
        val roll = Random.nextDouble()
        return when (bowlingPlan) {
            BowlingPlan.ATTACK_STUMPS -> when {
                roll < 0.45 -> WicketType.BOWLED
                roll < 0.80 -> WicketType.LBW
                else -> WicketType.CAUGHT
            }
            BowlingPlan.SHORT_BOUNCERS -> when {
                roll < 0.70 -> WicketType.CAUGHT
                roll < 0.85 -> WicketType.BOWLED
                else -> WicketType.RUN_OUT
            }
            BowlingPlan.YORKER_BLITZ -> when {
                roll < 0.50 -> WicketType.BOWLED
                roll < 0.75 -> WicketType.LBW
                else -> WicketType.CAUGHT
            }
            else -> when {
                roll < 0.55 -> WicketType.CAUGHT
                roll < 0.75 -> WicketType.BOWLED
                roll < 0.90 -> WicketType.LBW
                roll < 0.96 -> WicketType.RUN_OUT
                else -> WicketType.STUMPED
            }
        }
    }

    private fun generateWicketCommentary(strikerName: String, bowlerName: String, wicketType: WicketType): String {
        return when (wicketType) {
            WicketType.BOWLED -> "TIMBER! $bowlerName shatters the stumps with an unplayable delivery! $strikerName has to walk back!"
            WicketType.CAUGHT -> "OUT! In the air... and CAUGHT! $strikerName goes for the big shot but mistimes it straight down the throat of the fielder! $bowlerName strikes!"
            WicketType.LBW -> "LOUD APPEAL... and GIVEN! Plumb in front! $bowlerName traps $strikerName right in front of middle stump!"
            WicketType.RUN_OUT -> "RUN OUT! Chaos between the wickets! Direct hit from point and $strikerName is well short of the crease!"
            WicketType.STUMPED -> "STUMPED! Beautiful drift and spin, $strikerName dragged out of the crease and the bails are whipped off in a flash!"
            WicketType.HIT_WICKET -> "HIT WICKET! Disastrous for $strikerName! Stepping back too deep into the crease and brushes the stumps!"
            WicketType.NONE -> "Wicket!"
        }
    }
}
