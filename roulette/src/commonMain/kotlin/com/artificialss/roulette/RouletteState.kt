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
 * By default all prizes have equal probability (random).
 * You can override selection by passing a custom [selector] suspend function
 * that returns the winning index — the wheel waits for it (e.g. server call).
 *
 * @param prizeCount Number of prizes on the wheel
 * @param spinDurationMs Animation duration
 * @param selector Optional suspend function that returns the winning prize index.
 *                 If null, uses random with equal odds.
 *                 Use this to let a server decide the winner, or implement custom logic.
 */
@Stable
class RouletteState(
    private val prizeCount: Int,
    private val spinDurationMs: Int,
    private val selector: (suspend (prizeCount: Int) -> Int)? = null
) {
    internal val rotation = Animatable(0f)
    var isSpinning: Boolean = false
        internal set
    var lastWinnerIndex: Int = -1
        internal set

    /**
     * Spin the wheel. Picks a winner via [selector] (or random if null),
     * then animates to land on that segment.
     *
     * @return Index of the winning prize.
     */
    suspend fun spin(): Int {
        if (isSpinning || prizeCount <= 0) return lastWinnerIndex

        isSpinning = true

        // Pick winner — use custom selector or equal-odds random
        val winnerIndex = if (prizeCount == 1) 0
        else {
            val picked = selector?.invoke(prizeCount) ?: Random.nextInt(prizeCount)
            picked.coerceIn(0, prizeCount - 1)
        }

        val segmentAngle = 360f / prizeCount
        val targetCenter = winnerIndex * segmentAngle + segmentAngle / 2f
        val fullSpins = (4 + Random.nextInt(3)) * 360f
        val currentAngle = rotation.value % 360f
        val targetAngle = rotation.value + fullSpins + (360f - targetCenter - currentAngle + 360f) % 360f

        rotation.animateTo(
            targetAngle,
            tween(spinDurationMs, easing = EaseOutCubic)
        )

        lastWinnerIndex = winnerIndex
        isSpinning = false
        return winnerIndex
    }
}

/**
 * Create and remember a [RouletteState].
 *
 * @param prizeCount Number of prizes on the wheel.
 * @param spinDurationMs Spin animation duration.
 * @param selector Optional suspend function that returns the winning index.
 *                 The wheel starts spinning immediately, but waits for this
 *                 function to return before calculating where to land.
 *                 Perfect for server-side prize selection.
 */
@Composable
fun rememberRouletteState(
    prizeCount: Int,
    spinDurationMs: Int = 4000,
    selector: (suspend (prizeCount: Int) -> Int)? = null
): RouletteState {
    return remember(prizeCount, spinDurationMs) {
        RouletteState(prizeCount, spinDurationMs, selector)
    }
}
