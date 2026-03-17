package com.artificialss.roulette

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
 * If [Prize.icon] is null, a default star icon is drawn.
 *
 * @param prizes List of prizes (min 1). Equal probability for all.
 * @param onResult Called with the winning [Prize] after spin completes.
 * @param style Visual configuration (colors, spin speed).
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
 *
 * @param prizes List of prizes (min 1). Equal probability for all.
 * @param onResult Called with the winning [Prize] after spin completes.
 * @param style Visual configuration (colors, spin speed).
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

    // Animate transitions between spinning and stopped states
    val iconScale by animateFloatAsState(
        targetValue = if (isSpinning) 1.8f else 1f,
        animationSpec = tween(400)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (isSpinning) 0f else 1f,
        animationSpec = tween(if (isSpinning) 200 else 500)
    )
    val titleScale by animateFloatAsState(
        targetValue = if (isSpinning) 1.35f else 1f,
        animationSpec = tween(400)
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
        val iconSize = (when {
            count <= 4 -> 28f
            count <= 8 -> 22f
            count <= 12 -> 16f
            else -> 12f
        } * sizeScale).dp

        // Canvas wheel
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
            drawCircle(style.borderColor.copy(alpha = 0.3f), radius = radius * 0.93f, center = Offset(cx, cy), style = Stroke(1.5f))

            // Center hub
            val centerR = radius * 0.1f
            drawCircle(style.centerColor, radius = centerR + 3f, center = Offset(cx, cy))
            drawCircle(style.borderColor, radius = centerR + 3f, center = Offset(cx, cy), style = Stroke(2f))
            drawCircle(style.centerColor, radius = centerR, center = Offset(cx, cy))

            // Pointer — right side, pointing left (selected prize is right-side-up here)
            drawPointerRight(cx + radius + 6f, cy, w * 0.04f, style.pointerColor)
        }

        // Overlay: labels on each segment — no flip, items rotate naturally
        val density = LocalDensity.current
        val sizePx = with(density) { size.toPx() }
        val radius = sizePx / 2f - 4f

        prizes.forEachIndexed { i, prize ->
            val angle = if (count == 1) 0f
            else currentRotation + (-180f + i * segmentAngle + segmentAngle / 2f)

            val angleRad = angle * PI.toFloat() / 180f
            val labelR = radius * 0.58f
            val offsetX = with(density) { (labelR * cos(angleRad)).toDp() }
            val offsetY = with(density) { (labelR * sin(angleRad)).toDp() }
            val maxLabelWidth = with(density) { (radius * (if (count <= 6) 0.38f else 0.32f)).toDp() }

            Box(
                modifier = Modifier.offset(x = offsetX, y = offsetY).rotate(angle),
                contentAlignment = Alignment.Center
            ) {
                if (showIcons) {
                    // Icon mode: Row layout (icon in front of text)
                    // When spinning: text fades out, icon grows
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.widthIn(max = maxLabelWidth)
                    ) {
                        Box(
                            Modifier.size(iconSize).scale(iconScale),
                            contentAlignment = Alignment.Center
                        ) {
                            if (prize.icon != null) {
                                prize.icon.invoke()
                            } else {
                                DefaultPrizeIcon(iconSize, prize.textColor)
                            }
                        }
                        // Text column — fades out when spinning
                        if (textAlpha > 0.01f) {
                            Spacer(Modifier.width(3.dp))
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
                    // Text-only mode: title grows when spinning, description fades out
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.widthIn(max = maxLabelWidth)
                    ) {
                        Text(
                            prize.name,
                            color = prize.textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = titleSp * titleScale,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            lineHeight = titleSp * titleScale * 1.1f
                        )
                        if (prize.description.isNotEmpty() && textAlpha > 0.01f) {
                            Text(
                                prize.description,
                                color = prize.textColor.copy(alpha = 0.6f * textAlpha),
                                fontSize = descSp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
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

@Composable
private fun DefaultPrizeIcon(size: Dp, tint: Color) {
    Canvas(Modifier.size(size)) {
        val s = this.size.width
        val cx = s / 2f
        val cy = s / 2f
        val r = s * 0.4f
        val path = Path().apply {
            for (i in 0 until 5) {
                val outerAngle = (-90f + i * 72f) * PI.toFloat() / 180f
                val innerAngle = (-90f + i * 72f + 36f) * PI.toFloat() / 180f
                val ox = cx + r * cos(outerAngle)
                val oy = cy + r * sin(outerAngle)
                val ix = cx + r * 0.4f * cos(innerAngle)
                val iy = cy + r * 0.4f * sin(innerAngle)
                if (i == 0) moveTo(ox, oy) else lineTo(ox, oy)
                lineTo(ix, iy)
            }
            close()
        }
        drawPath(path, tint.copy(alpha = 0.8f))
    }
}

/** Pointer on the right side, pointing left toward the wheel center. */
private fun DrawScope.drawPointerRight(tipX: Float, cy: Float, sz: Float, color: Color) {
    val path = Path().apply {
        moveTo(tipX, cy)                            // tip pointing left
        lineTo(tipX + sz * 1.8f, cy - sz)           // top-right
        lineTo(tipX + sz * 1.8f, cy + sz)           // bottom-right
        close()
    }
    drawPath(path, color)
    drawPath(path, Color.Black.copy(alpha = 0.3f), style = Stroke(1f))
}
