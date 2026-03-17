package com.artificialss.roulette

import androidx.compose.ui.graphics.Color

/**
 * A single segment/prize on the roulette wheel.
 *
 * @param label Display text on the segment
 * @param color Background color of the segment
 * @param weight Relative probability weight (higher = more likely)
 * @param icon Optional emoji or short icon text drawn in the segment
 */
data class RouletteSegment(
    val label: String,
    val color: Color,
    val weight: Double = 1.0,
    val icon: String = ""
)

/**
 * Configuration for the roulette wheel appearance and behavior.
 */
data class RouletteConfig(
    val segments: List<RouletteSegment>,
    val spinDurationMs: Int = 4000,
    val borderColor: Color = Color.White,
    val borderWidth: Float = 4f,
    val pointerColor: Color = Color.White,
    val textColor: Color = Color.White,
    val centerColor: Color = Color(0xFF1A1A1A),
    val centerRadius: Float = 0.12f, // fraction of wheel radius
)
