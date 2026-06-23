package com.vasilisinaazbuka.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun BimEmotionsScreen(onBackClick: () -> Unit) {
    val emotionsList = mapOf(
        "🐶😊" to "Радость", "🐶😢" to "Грусть", "🐶😠" to "Злость",
        "🐶😮" to "Удивление", "🐶😨" to "Страх", "😴" to "Сон",
        "🍖" to "Голод", "🎾" to "Игра", "❤️" to "Любовь", "🤒" to "Болезнь"
    )
    var currentEmotionPair by remember { mutableStateOf(emotionsList.entries.random()) }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Что чувствует Бим?", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(32.dp))
            
            // Заглушка для картинки (bim_happy.png и т.д.)
            Text(currentEmotionPair.key, fontSize = 120.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                // Генерация 3 вариантов (1 правильный, 2 случайных)
                val options = remember(currentEmotionPair) {
                    (emotionsList.values.shuffled().take(2) + currentEmotionPair.value).shuffled()
                }
                
                options.forEach { option ->
                    Button(
                        onClick = { if (option == currentEmotionPair.value) currentEmotionPair = emotionsList.entries.random() },
                        modifier = Modifier.height(80.dp).width(200.dp)
                    ) {
                        Text(option, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
