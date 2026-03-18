package com.artificialss.roulette.demo

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composeroulette.demo.generated.resources.Res
import composeroulette.demo.generated.resources.*
import org.jetbrains.compose.resources.painterResource
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
                Prize("premium", "Premium Forever", "Lifetime access", Color(0xFF1565C0), icon = { PrizeIcon(Res.drawable.ic_crown) }),
                Prize("trial7", "7-Day Trial", "Try premium free", Color(0xFF347E67), icon = { PrizeIcon(Res.drawable.ic_gift) }),
                Prize("coins50", "50 Coins", "Bonus credits", Color(0xFF885484), icon = { PrizeIcon(Res.drawable.ic_coin) }),
                Prize("nothing1", "Try Again", "Better luck next time", Color(0xFF2A2A2A), icon = { PrizeIcon(Res.drawable.ic_tryagain) }, tryAgain = true),
                Prize("trial3", "3-Day Trial", "Quick taste", Color(0xFF574A40), icon = { PrizeIcon(Res.drawable.ic_hourglass) }),
                Prize("coins100", "100 Coins", "Big bonus!", Color(0xFFF87434), icon = { PrizeIcon(Res.drawable.ic_moneybag) }),
                Prize("nothing2", "Try Again", "So close...", Color(0xFF1E1E1E), icon = { PrizeIcon(Res.drawable.ic_slots) }, tryAgain = true),
                Prize("trial1", "1-Day Trial", "One day peek", Color(0xFF347E67).copy(alpha = 0.7f), icon = { PrizeIcon(Res.drawable.ic_star) }),
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

@Composable
private fun PrizeIcon(res: org.jetbrains.compose.resources.DrawableResource) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}
