package com.artificialss.roulette

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A single prize on the roulette wheel.
 *
 * @param name Prize title (1 line, ellipsis if too long)
 * @param description Prize subtitle (max 2 lines, ellipsis if too long)
 * @param color Background color of this segment
 * @param textColor Color for name and description text
 * @param icon Optional composable icon. Pass null for default star icon.
 *             Icon renders at the size of title + 1 description line.
 */
data class Prize(
    val id: String,
    val name: String,
    val description: String = "",
    val color: Color = Color(0xFF2A2A2A),
    val textColor: Color = Color.White,
    val icon: (@Composable () -> Unit)? = null
)

/**
 * Visual configuration for the roulette wheel.
 * Does NOT include prizes — those go directly to the composable.
 *
 * @param borderColor Color of the outer ring and center hub border
 * @param pointerColor Color of the selection pointer triangle at the top
 * @param backgroundColor Color behind the wheel (visible if wheel doesn't fill)
 * @param centerColor Color of the center hub circle
 * @param spinDurationMs Duration of the spin animation in milliseconds
 */
data class RouletteStyle(
    val borderColor: Color = Color.White,
    val pointerColor: Color = Color.White,
    val backgroundColor: Color = Color(0xFF0A0A0A),
    val centerColor: Color = Color(0xFF1A1A1A),
    val spinDurationMs: Int = 4000
)
