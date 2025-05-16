package com.droidcon.habitsync.ui.screens.motivation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotivationScreen() {
    val quote = remember { mutableStateOf("Believe in yourself and all that you are.") }
    val gradientColors = listOf(Color(0xFF6200EA), Color(0xFFB388FF), Color(0xFF00C4B4))
    val brush = Brush.verticalGradient(colors = gradientColors)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .shadow(12.dp), 
            backgroundColor = Color.White.copy(alpha = 0.9f),
            elevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Motivation",
                    tint = Color(0xFF6200EA),
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = quote.value,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { quote.value = getNewMotivationalQuote() },
                ) {
                    Text("Get Inspired!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

fun getNewMotivationalQuote(): String {
    val quotes = listOf(
        "You are stronger than you think!",
        "Keep going. Everything you need will come to you at the right time.",
        "Success is the sum of small efforts repeated daily.",
        "Do what you can, with what you have, where you are."
    )
    return quotes.random()
}
