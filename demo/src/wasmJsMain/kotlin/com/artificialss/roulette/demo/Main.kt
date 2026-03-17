package com.artificialss.roulette.demo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeViewport
import com.artificialss.roulette.Prize
import com.artificialss.roulette.RouletteStyle
import com.artificialss.roulette.RouletteWheel
import com.artificialss.roulette.RouletteWheelSimple
import com.artificialss.roulette.rememberRouletteState
import kotlinx.browser.document
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.getElementById("ComposeTarget")!!) {
        val scope = rememberCoroutineScope()
        var result1 by remember { mutableStateOf("") }
        var result2 by remember { mutableStateOf("") }

        val prizes = remember {
            listOf(
                Prize("premium", "Premium Forever", "Lifetime access", Color(0xFFFFC84C), Color(0xFF1A1A1A), icon = { CrownIcon() }),
                Prize("trial7", "7-Day Trial", "Try premium free", Color(0xFF347E67), icon = { GiftIcon() }),
                Prize("coins50", "50 Coins", "Bonus credits", Color(0xFF885484), icon = { CoinIcon() }),
                Prize("nothing1", "Try Again", "Better luck next time", Color(0xFF2A2A2A), tryAgain = true),
                Prize("trial3", "3-Day Trial", "Quick taste", Color(0xFF574A40), icon = { ClockIcon() }),
                Prize("coins100", "100 Coins", "Big bonus!", Color(0xFFF87434), Color(0xFF1A1A1A), icon = { CoinIcon() }),
                Prize("nothing2", "Try Again", "So close...", Color(0xFF1E1E1E), tryAgain = true),
                Prize("trial1", "1-Day Trial", "One day peek", Color(0xFF347E67).copy(alpha = 0.7f)),
            )
        }

        val goldStyle = remember { RouletteStyle(borderColor = Color(0xFFFFC84C), pointerColor = Color(0xFFFFC84C), centerColor = Color(0xFF0A0A0A)) }
        val plumStyle = remember { RouletteStyle(borderColor = Color(0xFF885484), pointerColor = Color(0xFF885484), centerColor = Color(0xFF0A0A0A)) }

        val stateIcons = rememberRouletteState(prizes.size)
        val stateSimple = rememberRouletteState(prizes.size)

        BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
            val isWide = maxWidth > maxHeight

            if (isWide) {
                Row(Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                        RouletteWheel(prizes = prizes, state = stateIcons, style = goldStyle, modifier = Modifier.fillMaxWidth().weight(1f))
                        Spacer(Modifier.height(8.dp))
                        if (result1.isNotEmpty()) ResultText(result1)
                        Spacer(Modifier.height(8.dp))
                        SpinButton(stateIcons.isSpinning) { scope.launch { result1 = ""; val i = stateIcons.spin(); result1 = prizes[i].name } }
                    }
                    Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                        RouletteWheelSimple(prizes = prizes, state = stateSimple, style = plumStyle, modifier = Modifier.fillMaxWidth().weight(1f))
                        Spacer(Modifier.height(8.dp))
                        if (result2.isNotEmpty()) ResultText(result2)
                        Spacer(Modifier.height(8.dp))
                        SpinButton(stateSimple.isSpinning, Color(0xFF885484)) { scope.launch { result2 = ""; val i = stateSimple.spin(); result2 = prizes[i].name } }
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RouletteWheel(prizes = prizes, state = stateIcons, style = goldStyle, modifier = Modifier.fillMaxWidth().height(340.dp))
                    Spacer(Modifier.height(8.dp))
                    if (result1.isNotEmpty()) ResultText(result1)
                    Spacer(Modifier.height(8.dp))
                    SpinButton(stateIcons.isSpinning) { scope.launch { result1 = ""; val i = stateIcons.spin(); result1 = prizes[i].name } }

                    Spacer(Modifier.height(32.dp))

                    RouletteWheelSimple(prizes = prizes, state = stateSimple, style = plumStyle, modifier = Modifier.fillMaxWidth().height(340.dp))
                    Spacer(Modifier.height(8.dp))
                    if (result2.isNotEmpty()) ResultText(result2)
                    Spacer(Modifier.height(8.dp))
                    SpinButton(stateSimple.isSpinning, Color(0xFF885484)) { scope.launch { result2 = ""; val i = stateSimple.spin(); result2 = prizes[i].name } }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ResultText(text: String) {
    Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun SpinButton(isSpinning: Boolean, color: Color = Color(0xFFFFC84C), onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSpinning) Color(0xFF2A2A2A) else color)
            .clickable(enabled = !isSpinning) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (isSpinning) "Spinning..." else "SPIN",
            color = if (isSpinning) Color(0xFF8A8A8A) else Color(0xFF0A0A0A),
            fontWeight = FontWeight.Bold, fontSize = 14.sp
        )
    }
}

// ── Premium crown with gems ──
@Composable
private fun CrownIcon() {
    Canvas(Modifier.size(22.dp)) {
        val s = size.width
        // Crown body
        val crown = Path().apply {
            moveTo(s * 0.08f, s * 0.82f); lineTo(s * 0.08f, s * 0.32f)
            lineTo(s * 0.28f, s * 0.48f); lineTo(s * 0.5f, s * 0.1f)
            lineTo(s * 0.72f, s * 0.48f); lineTo(s * 0.92f, s * 0.32f)
            lineTo(s * 0.92f, s * 0.82f); close()
        }
        drawPath(crown, Color(0xFFFFC84C))
        // Crown band
        drawRect(Color(0xFFE5A830), Offset(s * 0.08f, s * 0.72f), Size(s * 0.84f, s * 0.1f))
        // Gems
        drawCircle(Color(0xFFFF4444), s * 0.045f, Offset(s * 0.3f, s * 0.77f))
        drawCircle(Color(0xFF4488FF), s * 0.045f, Offset(s * 0.5f, s * 0.77f))
        drawCircle(Color(0xFF44DD44), s * 0.045f, Offset(s * 0.7f, s * 0.77f))
        // Peak dots
        drawCircle(Color(0xFFFFE082), s * 0.04f, Offset(s * 0.5f, s * 0.12f))
        drawCircle(Color(0xFFFFE082), s * 0.03f, Offset(s * 0.08f, s * 0.34f))
        drawCircle(Color(0xFFFFE082), s * 0.03f, Offset(s * 0.92f, s * 0.34f))
    }
}

// ── Gift box with ribbon bow ──
@Composable
private fun GiftIcon() {
    Canvas(Modifier.size(22.dp)) {
        val s = size.width
        // Box bottom
        drawRoundRect(Color(0xFF347E67), Offset(s * 0.12f, s * 0.45f),
            Size(s * 0.76f, s * 0.45f), CornerRadius(s * 0.06f))
        // Box lid
        drawRoundRect(Color(0xFF2D6B58), Offset(s * 0.08f, s * 0.35f),
            Size(s * 0.84f, s * 0.15f), CornerRadius(s * 0.04f))
        // Vertical ribbon
        drawRect(Color(0xFFFFC84C), Offset(s * 0.44f, s * 0.35f), Size(s * 0.12f, s * 0.55f))
        // Horizontal ribbon
        drawRect(Color(0xFFFFC84C), Offset(s * 0.08f, s * 0.5f), Size(s * 0.84f, s * 0.08f))
        // Bow loops
        drawCircle(Color(0xFFFFC84C), s * 0.09f, Offset(s * 0.38f, s * 0.28f))
        drawCircle(Color(0xFFFFC84C), s * 0.09f, Offset(s * 0.62f, s * 0.28f))
        drawCircle(Color(0xFFE5A830), s * 0.04f, Offset(s * 0.5f, s * 0.28f))
    }
}

// ── Gold coin with dollar sign ──
@Composable
private fun CoinIcon() {
    Canvas(Modifier.size(22.dp)) {
        val s = size.width
        val c = Offset(s / 2, s / 2)
        // Coin shadow
        drawCircle(Color(0xFFB8860B), s * 0.4f, Offset(c.x + s * 0.02f, c.y + s * 0.02f))
        // Coin body
        drawCircle(Color(0xFFFFC84C), s * 0.38f, c)
        // Inner ring
        drawCircle(Color(0xFFE5A830), s * 0.3f, c, style = Stroke(s * 0.04f))
        // Dollar sign — vertical line
        drawLine(Color(0xFFB8860B), Offset(c.x, c.y - s * 0.18f), Offset(c.x, c.y + s * 0.18f), strokeWidth = s * 0.06f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        // Dollar sign — S curves
        drawArc(Color(0xFFB8860B), 180f, 180f, false, style = Stroke(s * 0.05f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            topLeft = Offset(c.x - s * 0.12f, c.y - s * 0.16f), size = Size(s * 0.24f, s * 0.16f))
        drawArc(Color(0xFFB8860B), 0f, 180f, false, style = Stroke(s * 0.05f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            topLeft = Offset(c.x - s * 0.12f, c.y), size = Size(s * 0.24f, s * 0.16f))
    }
}

// ── Hourglass with sand ──
@Composable
private fun ClockIcon() {
    Canvas(Modifier.size(22.dp)) {
        val s = size.width
        val c = Offset(s / 2, s / 2)
        // Top half
        val top = Path().apply {
            moveTo(s * 0.2f, s * 0.12f); lineTo(s * 0.8f, s * 0.12f)
            lineTo(s * 0.55f, s * 0.48f); lineTo(s * 0.45f, s * 0.48f); close()
        }
        drawPath(top, Color.White.copy(alpha = 0.25f))
        drawPath(top, Color.White, style = Stroke(s * 0.04f, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        // Bottom half
        val bot = Path().apply {
            moveTo(s * 0.45f, s * 0.52f); lineTo(s * 0.55f, s * 0.52f)
            lineTo(s * 0.8f, s * 0.88f); lineTo(s * 0.2f, s * 0.88f); close()
        }
        drawPath(bot, Color.White.copy(alpha = 0.25f))
        drawPath(bot, Color.White, style = Stroke(s * 0.04f, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        // Sand top (triangle)
        val sandTop = Path().apply {
            moveTo(s * 0.35f, s * 0.2f); lineTo(s * 0.65f, s * 0.2f)
            lineTo(s * 0.52f, s * 0.38f); lineTo(s * 0.48f, s * 0.38f); close()
        }
        drawPath(sandTop, Color(0xFFFFC84C).copy(alpha = 0.7f))
        // Sand bottom (triangle)
        val sandBot = Path().apply {
            moveTo(s * 0.48f, s * 0.62f); lineTo(s * 0.52f, s * 0.62f)
            lineTo(s * 0.7f, s * 0.82f); lineTo(s * 0.3f, s * 0.82f); close()
        }
        drawPath(sandBot, Color(0xFFFFC84C))
        // Falling sand line
        drawLine(Color(0xFFFFC84C), Offset(c.x, s * 0.45f), Offset(c.x, s * 0.55f), strokeWidth = s * 0.03f)
    }
}
