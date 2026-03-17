package com.artificialss.roulette

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlin.random.Random

/**
 * Holds the spin state for the roulette wheel.
 * Create via [rememberRouletteState].
 */
@Stable
class RouletteState(
    val config: RouletteConfig
) {
    internal val rotation = Animatable(0f)
    var isSpinning: Boolean = false
        internal set
    var lastResult: RouletteSegment? = null
        internal set

    /**
     * Spin the wheel. Picks a result based on segment weights,
     * then animates to land on that segment.
     *
     * @return The winning segment
     */
    suspend fun spin(): RouletteSegment {
        if (isSpinning) return lastResult ?: config.segments.first()
        isSpinning = true

        // Pick winner by weighted random
        val totalWeight = config.segments.sumOf { it.weight }
        var pick = Random.nextDouble() * totalWeight
        var winnerIndex = 0
        for (i in config.segments.indices) {
            pick -= config.segments[i].weight
            if (pick <= 0) { winnerIndex = i; break }
        }

        // Calculate target angle
        val segmentAngle = 360f / config.segments.size
        val targetSegmentCenter = winnerIndex * segmentAngle + segmentAngle / 2f
        val fullSpins = 5 * 360f
        val currentAngle = rotation.value % 360f
        val targetAngle = rotation.value + fullSpins + (360f - targetSegmentCenter - currentAngle + 360f) % 360f

        rotation.animateTo(
            targetAngle,
            tween(config.spinDurationMs, easing = EaseOutCubic)
        )

        lastResult = config.segments[winnerIndex]
        isSpinning = false
        return config.segments[winnerIndex]
    }
}

@Composable
fun rememberRouletteState(config: RouletteConfig): RouletteState {
    return remember(config) { RouletteState(config) }
}
