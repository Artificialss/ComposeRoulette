package com.artificialss.roulette

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Roulette wheel WITH icons.
 * Each segment shows: icon (left) + name + description in a row.
 * While spinning, text hides and icon grows. When stopped, text animates back.
 */
@Composable
fun RouletteWheel(
    prizes: List<Prize>,
    state: RouletteState,
    onResult: (Prize) -> Unit = {},
    style: RouletteStyle = RouletteStyle(),
    modifier: Modifier = Modifier
) {
    RouletteWheelInternal(
        prizes = prizes,
        state = state,
        onResult = onResult,
        style = style,
        showIcons = true,
        modifier = modifier
    )
}

/**
 * Roulette wheel WITHOUT icons (text only).
 * While spinning, description hides and title grows. When stopped, description animates back.
 */
@Composable
fun RouletteWheelSimple(
    prizes: List<Prize>,
    state: RouletteState,
    onResult: (Prize) -> Unit = {},
    style: RouletteStyle = RouletteStyle(),
    modifier: Modifier = Modifier
) {
    RouletteWheelInternal(
        prizes = prizes,
        state = state,
        onResult = onResult,
        style = style,
        showIcons = false,
        modifier = modifier
    )
}

@Composable
private fun RouletteWheelInternal(
    prizes: List<Prize>,
    state: RouletteState,
    onResult: (Prize) -> Unit,
    style: RouletteStyle,
    showIcons: Boolean,
    modifier: Modifier
) {
    if (prizes.isEmpty()) return

    val currentRotation = state.rotation.value
    val count = prizes.size
    val segmentAngle = 360f / count
    val isSpinning = state.isSpinning

    // Animate transitions: text fades when spinning
    val textAlpha by animateFloatAsState(
        targetValue = if (isSpinning) 0f else 1f,
        animationSpec = tween(if (isSpinning) 200 else 400)
    )
    val titleScale by animateFloatAsState(
        targetValue = if (isSpinning) 1.35f else 1f,
        animationSpec = tween(if (isSpinning) 400 else 300)
    )

    BoxWithConstraints(modifier.padding(15.dp), contentAlignment = Alignment.Center) {
        val size = if (maxWidth < maxHeight) maxWidth else maxHeight

        val sizeScale = (size.value / 300f).coerceIn(0.6f, 1.5f)
        val titleSp = (when {
            count <= 4 -> 13f
            count <= 8 -> 11f
            count <= 12 -> 9f
            else -> 7f
        } * sizeScale).sp
        val descSp = (when {
            count <= 4 -> 10f
            count <= 8 -> 8f
            count <= 12 -> 7f
            else -> 6f
        } * sizeScale).sp
        // Canvas: colored segments + border + hub + pointer
        Canvas(Modifier.size(size)) {
            val w = this.size.width
            val cx = w / 2f
            val cy = w / 2f
            val radius = w / 2f - 4f

            if (count == 1) {
                drawCircle(prizes[0].color, radius = radius, center = Offset(cx, cy))
            } else {
                rotate(currentRotation, Offset(cx, cy)) {
                    prizes.forEachIndexed { i, prize ->
                        val startAngle = -180f + i * segmentAngle
                        drawArc(
                            color = prize.color,
                            startAngle = startAngle,
                            sweepAngle = segmentAngle,
                            useCenter = true,
                            topLeft = Offset(cx - radius, cy - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        val lineRad = startAngle * PI.toFloat() / 180f
                        drawLine(
                            color = style.borderColor.copy(alpha = 0.25f),
                            start = Offset(cx, cy),
                            end = Offset(cx + radius * cos(lineRad), cy + radius * sin(lineRad)),
                            strokeWidth = 1.5f
                        )
                    }
                }
            }

            // Outer border
            drawCircle(style.borderColor, radius = radius, center = Offset(cx, cy), style = Stroke(4f))

            // Center hub
            val centerR = radius * 0.1f
            drawCircle(style.centerColor, radius = centerR + 3f, center = Offset(cx, cy))
            drawCircle(style.borderColor, radius = centerR + 3f, center = Offset(cx, cy), style = Stroke(2f))
            drawCircle(style.centerColor, radius = centerR, center = Offset(cx, cy))

            // Pointer — left side, pointing right
            drawPointerLeft(cx - radius - 6f, cy, w * 0.04f, style.pointerColor)
        }

        // Overlay: labels contained within segment boundaries, filling all available space.
        val density = LocalDensity.current
        val sizePx = with(density) { size.toPx() }
        val radius = sizePx / 2f - 4f
        val edgePaddingPx = with(density) { 6.dp.toPx() }  // breathing room from outer rim
        val hubR = radius * 0.14f
        val usableLength = radius - edgePaddingPx - hubR
        // Both modes: anchor at segment midpoint so box is fully inside the segment
        val segMidR = radius - edgePaddingPx - usableLength / 2f
        val chordMidPx = 2f * segMidR * sin((segmentAngle / 2f) * PI.toFloat() / 180f) * 0.82f
        val maxLabelWidth = with(density) { usableLength.toDp() }
        val iconLabelHeight = with(density) { chordMidPx.toDp() }
        // Text mode: taller chord + full usable width for more space
        val chordTextPx = 2f * segMidR * sin((segmentAngle / 2f) * PI.toFloat() / 180f) * 0.92f
        val textLabelHeight = with(density) { chordTextPx.toDp() }

        // Icon animates from ~42% of segment height (rest) to ~65% (spinning)
        val iconSizePx by animateFloatAsState(
            targetValue = if (isSpinning) chordMidPx * 0.65f else chordMidPx * 0.42f,
            animationSpec = tween(if (isSpinning) 400 else 300)
        )
        val animatedIconSize = with(density) { iconSizePx.toDp() }

        prizes.forEachIndexed { i, prize ->
            val angle = if (count == 1) 0f
            else currentRotation + (-180f + i * segmentAngle + segmentAngle / 2f)

            val angleRad = angle * PI.toFloat() / 180f
            val labelHeight = if (showIcons) iconLabelHeight else textLabelHeight
            // Icon mode: centered at midpoint. Text mode: outer-edge anchor so text starts at rim.
            val anchorR = if (showIcons) segMidR else (radius - edgePaddingPx - usableLength / 2f)
            val offsetX = with(density) { (anchorR * cos(angleRad)).toDp() }
            val offsetY = with(density) { (anchorR * sin(angleRad)).toDp() }
            val displayAngle = angle + 180f

            Box(
                modifier = Modifier
                    .size(maxLabelWidth, labelHeight)
                    .offset(x = offsetX, y = offsetY)
                    .rotate(displayAngle),
                contentAlignment = if (showIcons) Alignment.Center else Alignment.CenterStart
            ) {
                if (showIcons) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize().padding(start = 4.dp, end = 2.dp)
                    ) {
                        Box(
                            Modifier.size(animatedIconSize),
                            contentAlignment = Alignment.Center
                        ) {
                            prize.icon?.invoke()
                        }
                        if (textAlpha > 0.01f) {
                            Spacer(Modifier.width(4.dp))
                            Column(Modifier.weight(1f, fill = false).alpha(textAlpha)) {
                                Text(
                                    prize.name,
                                    color = prize.textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = titleSp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start,
                                    lineHeight = titleSp * 1.1f
                                )
                                if (prize.description.isNotEmpty()) {
                                    Text(
                                        prize.description,
                                        color = prize.textColor.copy(alpha = 0.6f),
                                        fontSize = descSp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Start,
                                        lineHeight = descSp * 1.1f
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Text-only mode: anchored at outer rim, reads inward, start-aligned
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxSize().padding(start = 4.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            prize.name,
                            color = prize.textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = titleSp * titleScale,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            lineHeight = titleSp * titleScale * 1.1f
                        )
                        if (prize.description.isNotEmpty() && textAlpha > 0.01f) {
                            Text(
                                prize.description,
                                color = prize.textColor.copy(alpha = 0.6f * textAlpha),
                                fontSize = descSp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                lineHeight = descSp * 1.1f,
                                modifier = Modifier.alpha(textAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun DrawScope.drawPointerLeft(tipX: Float, cy: Float, sz: Float, color: Color) {
    val path = Path().apply {
        moveTo(tipX, cy)                            // tip pointing right
        lineTo(tipX - sz * 1.8f, cy - sz)           // top-left
        lineTo(tipX - sz * 1.8f, cy + sz)           // bottom-left
        close()
    }
    drawPath(path, color)
    drawPath(path, Color.Black.copy(alpha = 0.3f), style = Stroke(1f))
}
