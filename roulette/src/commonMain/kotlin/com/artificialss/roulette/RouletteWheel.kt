package com.artificialss.roulette

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Compose Roulette Wheel by Artificialss.
 *
 * A fully Canvas-drawn, animated roulette wheel.
 * No dependencies beyond Compose Foundation.
 *
 * @param state The roulette state (create via [rememberRouletteState])
 * @param size Diameter of the wheel
 * @param modifier Standard Compose modifier
 */
@Composable
fun RouletteWheel(
    state: RouletteState,
    size: Dp = 300.dp,
    modifier: Modifier = Modifier
) {
    val config = state.config
    val currentRotation = state.rotation.value

    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val wheelSize = this.size.width
            val cx = wheelSize / 2f
            val cy = wheelSize / 2f
            val radius = wheelSize / 2f - config.borderWidth

            val segmentAngle = 360f / config.segments.size

            // Draw wheel segments (rotated)
            rotate(currentRotation, Offset(cx, cy)) {
                config.segments.forEachIndexed { i, segment ->
                    val startAngle = -90f + i * segmentAngle

                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = segmentAngle,
                        useCenter = true,
                        topLeft = Offset(cx - radius, cy - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Segment divider line
                    val lineAngle = startAngle * PI.toFloat() / 180f
                    drawLine(
                        color = config.borderColor.copy(alpha = 0.3f),
                        start = Offset(cx, cy),
                        end = Offset(cx + radius * cos(lineAngle), cy + radius * sin(lineAngle)),
                        strokeWidth = 1.5f
                    )

                    // Dot marker at segment center
                    val labelAngle = (startAngle + segmentAngle / 2f) * PI.toFloat() / 180f
                    val dotRadius = radius * 0.7f
                    drawCircle(
                        color = config.textColor.copy(alpha = 0.8f),
                        radius = wheelSize * 0.012f,
                        center = Offset(cx + dotRadius * cos(labelAngle), cy + dotRadius * sin(labelAngle))
                    )

                    // Icon dot near edge
                    if (segment.icon.isNotEmpty()) {
                        val iconR = radius * 0.88f
                        drawCircle(
                            color = Color.White.copy(alpha = 0.2f),
                            radius = wheelSize * 0.025f,
                            center = Offset(cx + iconR * cos(labelAngle), cy + iconR * sin(labelAngle))
                        )
                    }
                }
            }

            // Outer border
            drawCircle(config.borderColor, radius = radius, center = Offset(cx, cy), style = Stroke(config.borderWidth))
            // Inner ring
            drawCircle(config.borderColor.copy(alpha = 0.4f), radius = radius * 0.92f, center = Offset(cx, cy), style = Stroke(1.5f))

            // Center hub
            val centerR = radius * config.centerRadius
            drawCircle(config.centerColor, radius = centerR + 3f, center = Offset(cx, cy))
            drawCircle(config.borderColor, radius = centerR + 3f, center = Offset(cx, cy), style = Stroke(2f))
            drawCircle(config.centerColor, radius = centerR, center = Offset(cx, cy))

            // Pointer at top
            drawPointer(cx, cy - radius - config.borderWidth * 1.5f, wheelSize * 0.04f, config.pointerColor)
        }
    }
}

private fun DrawScope.drawPointer(cx: Float, tipY: Float, sz: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, tipY)
        lineTo(cx - sz, tipY - sz * 1.8f)
        lineTo(cx + sz, tipY - sz * 1.8f)
        close()
    }
    drawPath(path, color)
    drawPath(path, Color.Black.copy(alpha = 0.3f), style = Stroke(1f))
}
