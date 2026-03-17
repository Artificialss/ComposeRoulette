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
import androidx.compose.ui.text.style.TextAlign
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
        var result1 by remember { mutableStateOf("Tap SPIN") }
        var result2 by remember { mutableStateOf("Tap SPIN") }

        val prizes = remember {
            listOf(
                Prize("Premium Forever", "Lifetime access", Color(0xFFFFC84C), Color(0xFF1A1A1A), icon = { CrownIcon() }),
                Prize("7-Day Trial", "Try premium free", Color(0xFF347E67), icon = { GiftIcon() }),
                Prize("50 Coins", "Bonus credits", Color(0xFF885484), icon = { CoinIcon() }),
                Prize("Try Again", "Better luck next time", Color(0xFF2A2A2A)),
                Prize("3-Day Trial", "Quick taste", Color(0xFF574A40), icon = { ClockIcon() }),
                Prize("100 Coins", "Big bonus!", Color(0xFFF87434), Color(0xFF1A1A1A), icon = { CoinIcon() }),
                Prize("Try Again", "So close...", Color(0xFF1E1E1E)),
                Prize("1-Day Trial", "One day peek", Color(0xFF347E67).copy(alpha = 0.7f)),
            )
        }

        val goldStyle = remember { RouletteStyle(borderColor = Color(0xFFFFC84C), pointerColor = Color(0xFFFFC84C), centerColor = Color(0xFF0A0A0A)) }
        val plumStyle = remember { RouletteStyle(borderColor = Color(0xFF885484), pointerColor = Color(0xFF885484), centerColor = Color(0xFF0A0A0A)) }

        val stateIcons = rememberRouletteState(prizes.size)
        val stateSimple = rememberRouletteState(prizes.size)

        BoxWithConstraints(Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
            val isWide = maxWidth > maxHeight  // landscape

            if (isWide) {
                // Side by side
                Row(Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Left: with icons
                    WheelPanel(
                        title = "RouletteWheel",
                        subtitle = "with icons",
                        accentColor = Color(0xFFFFC84C),
                        result = result1,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        RouletteWheel(prizes = prizes, state = stateIcons, style = goldStyle, modifier = Modifier.fillMaxWidth().weight(1f))
                        Spacer(Modifier.height(12.dp))
                        SpinButton(stateIcons.isSpinning, Color(0xFFFFC84C)) {
                            scope.launch { result1 = "Spinning..."; val i = stateIcons.spin(); result1 = prizes[i].name }
                        }
                    }
                    // Right: simple
                    WheelPanel(
                        title = "RouletteWheelSimple",
                        subtitle = "text only",
                        accentColor = Color(0xFF885484),
                        result = result2,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        RouletteWheelSimple(prizes = prizes, state = stateSimple, style = plumStyle, modifier = Modifier.fillMaxWidth().weight(1f))
                        Spacer(Modifier.height(12.dp))
                        SpinButton(stateSimple.isSpinning, Color(0xFF885484)) {
                            scope.launch { result2 = "Spinning..."; val i = stateSimple.spin(); result2 = prizes[i].name }
                        }
                    }
                }
            } else {
                // Stacked
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Compose Roulette", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("by Artificialss", color = Color(0xFF8A8A8A), fontSize = 11.sp)
                    Spacer(Modifier.height(24.dp))

                    WheelPanel("RouletteWheel", "with icons", Color(0xFFFFC84C), result1) {
                        RouletteWheel(prizes = prizes, state = stateIcons, style = goldStyle, modifier = Modifier.fillMaxWidth().height(320.dp))
                        Spacer(Modifier.height(12.dp))
                        SpinButton(stateIcons.isSpinning, Color(0xFFFFC84C)) {
                            scope.launch { result1 = "Spinning..."; val i = stateIcons.spin(); result1 = prizes[i].name }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    WheelPanel("RouletteWheelSimple", "text only", Color(0xFF885484), result2) {
                        RouletteWheelSimple(prizes = prizes, state = stateSimple, style = plumStyle, modifier = Modifier.fillMaxWidth().height(320.dp))
                        Spacer(Modifier.height(12.dp))
                        SpinButton(stateSimple.isSpinning, Color(0xFF885484)) {
                            scope.launch { result2 = "Spinning..."; val i = stateSimple.spin(); result2 = prizes[i].name }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Text("Made with coffee by Artificialss", color = Color(0xFF555555), fontSize = 11.sp)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun WheelPanel(
    title: String,
    subtitle: String,
    accentColor: Color,
    result: String,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Color(0xFF8A8A8A), fontSize = 10.sp)
        Spacer(Modifier.height(8.dp))
        content()
        Spacer(Modifier.height(8.dp))
        Text(result, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SpinButton(isSpinning: Boolean, color: Color = Color(0xFFFFC84C), onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
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

// ── Demo icons ──

@Composable private fun CrownIcon() { Canvas(Modifier.size(20.dp)) { val s = size.width; val p = Path().apply { moveTo(s*0.1f,s*0.8f);lineTo(s*0.1f,s*0.35f);lineTo(s*0.3f,s*0.5f);lineTo(s*0.5f,s*0.15f);lineTo(s*0.7f,s*0.5f);lineTo(s*0.9f,s*0.35f);lineTo(s*0.9f,s*0.8f);close() }; drawPath(p, Color(0xFFFFC84C)) } }
@Composable private fun GiftIcon() { Canvas(Modifier.size(20.dp)) { val s = size.width; drawRoundRect(Color(0xFF347E67), Offset(s*0.15f,s*0.4f), Size(s*0.7f,s*0.5f), CornerRadius(s*0.05f)); drawRect(Color.White.copy(alpha=0.4f), Offset(s*0.45f,s*0.4f), Size(s*0.1f,s*0.5f)); drawCircle(Color(0xFF347E67), s*0.12f, Offset(s*0.5f,s*0.3f)) } }
@Composable private fun CoinIcon() { Canvas(Modifier.size(20.dp)) { val s = size.width; drawCircle(Color(0xFFFFC84C), s*0.38f, Offset(s/2,s/2)); drawCircle(Color(0xFFFFC84C).copy(alpha=0.5f), s*0.28f, Offset(s/2,s/2), style=Stroke(s*0.06f)) } }
@Composable private fun ClockIcon() { Canvas(Modifier.size(20.dp)) { val s = size.width; val c = Offset(s/2,s/2); drawCircle(Color.White, s*0.38f, c, style=Stroke(s*0.06f)); drawLine(Color.White, c, Offset(c.x,c.y-s*0.22f), strokeWidth=s*0.06f); drawLine(Color.White, c, Offset(c.x+s*0.16f,c.y), strokeWidth=s*0.06f) } }
