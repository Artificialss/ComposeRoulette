package com.artificialss.roulette

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlin.random.Random

/**
 * Manages spin animation and prize selection.
 *
 * @param prizeCount Number of prizes on the wheel
 * @param spinDurationMs Animation duration
 */
@Stable
class RouletteState(
    private val prizeCount: Int,
    private val spinDurationMs: Int
) {
    internal val rotation = Animatable(0f)
    var isSpinning: Boolean = false
        internal set
    var lastWinnerIndex: Int = -1
        internal set

    /**
     * Spin the wheel with random equal-odds selection.
     *
     * @return Index of the winning prize.
     */
    suspend fun spin(): Int = spin(winnerIndex = null)

    /**
     * Spin the wheel to land on a specific prize.
     * Use this when your backend already decided the winner.
     *
     * @param winnerIndex The index of the prize to land on (0-based).
     * @return Index of the winning prize.
     */
    suspend fun spin(winnerIndex: Int?): Int {
        if (isSpinning || prizeCount <= 0) return lastWinnerIndex

        isSpinning = true

        val target = when {
            prizeCount == 1 -> 0
            winnerIndex != null -> winnerIndex.coerceIn(0, prizeCount - 1)
            else -> Random.nextInt(prizeCount)
        }

        val segmentAngle = 360f / prizeCount
        val neededRotation = 360f - target * segmentAngle - segmentAngle / 2f
        val fullSpins = (4 + Random.nextInt(3)) * 360f
        val currentAngle = rotation.value % 360f
        val targetAngle = rotation.value + fullSpins + (neededRotation - currentAngle + 360f) % 360f

        rotation.animateTo(
            targetAngle,
            tween(spinDurationMs, easing = EaseOutCubic)
        )

        lastWinnerIndex = target
        isSpinning = false
        return target
    }
}

/**
 * Create and remember a [RouletteState].
 *
 * @param prizeCount Number of prizes on the wheel.
 * @param spinDurationMs Spin animation duration.
 */
@Composable
fun rememberRouletteState(
    prizeCount: Int,
    spinDurationMs: Int = 4000
): RouletteState {
    return remember(prizeCount, spinDurationMs) {
        RouletteState(prizeCount, spinDurationMs)
    }
}
