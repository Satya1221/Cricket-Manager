package com.example.cricketmanager.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween

/** Motion tokens for the premium sports UI. */
object CricketMotion {
    val Emphasized: Easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    val Standard: Easing = FastOutSlowInEasing

    fun enter() = tween<Float>(260, easing = Emphasized)
    fun quick() = tween<Float>(180, easing = Emphasized)
    fun progress() = tween<Float>(600, easing = Emphasized)
    fun press() = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}
