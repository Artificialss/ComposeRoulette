package com.artificialss.roulette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import com.artificialss.roulette.RouletteConfig
import com.artificialss.roulette.RouletteSegment
import com.artificialss.roulette.RouletteWheel
import com.artificialss.roulette.rememberRouletteState
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.getElementById("ComposeTarget")!!) {
        val config = remember {
            RouletteConfig(
                segments = listOf(
                    RouletteSegment("Premium Forever", Color(0xFFFFC84C), weight = 0.5, icon = "👑"),
                    RouletteSegment("7-Day Trial", Color(0xFF347E67), weight = 10.0, icon = "🎁"),
                    RouletteSegment("50 Coins", Color(0xFF885484), weight = 15.0, icon = "🪙"),
                    RouletteSegment("Try Again", Color(0xFF2A2A2A), weight = 30.0),
                    RouletteSegment("3-Day Trial", Color(0xFF574A40), weight = 20.0, icon = "⏳"),
                    RouletteSegment("100 Coins", Color(0xFFF87434), weight = 10.0, icon = "🪙"),
                    RouletteSegment("Try Again", Color(0xFF1E1E1E), weight = 30.0),
                    RouletteSegment("1-Day Trial", Color(0xFF347E67).copy(alpha = 0.7f), weight = 25.0, icon = "⏳"),
                ),
                borderColor = Color(0xFFFFC84C),
                centerColor = Color(0xFF0A0A0A),
                pointerColor = Color(0xFFFFC84C)
            )
        }

        val state = rememberRouletteState(config)
        val scope = rememberCoroutineScope()
        var resultText by remember { mutableStateOf("Tap SPIN to play!") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Compose Roulette",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "by Artificialss",
                color = Color(0xFF8A8A8A),
                fontSize = 12.sp
            )

            Spacer(Modifier.height(32.dp))

            RouletteWheel(state = state, size = 320.dp)

            Spacer(Modifier.height(24.dp))

            // Spin button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (state.isSpinning) Color(0xFF2A2A2A) else Color(0xFFFFC84C))
                    .clickable(enabled = !state.isSpinning) {
                        scope.launch {
                            resultText = "Spinning..."
                            val result = state.spin()
                            resultText = "🎉 ${result.label}!"
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (state.isSpinning) "Spinning..." else "SPIN",
                    color = if (state.isSpinning) Color(0xFF8A8A8A) else Color(0xFF0A0A0A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                resultText,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
