package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun FeedBimScreen(onBackClick: () -> Unit) {
    // Состояние характеристик (в реальном приложении сохраняем в SharedPreferences/ViewModel)
    var fullness by remember { mutableFloatStateOf(50f) }
    var happiness by remember { mutableFloatStateOf(50f) }
    var energy by remember { mutableFloatStateOf(50f) }
    var currentTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE8F5E9))) {
        // Кнопка Назад
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // 1/3 экрана: Персонаж Бим и его статы
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when {
                        fullness > 80 && happiness > 80 -> "🐶❤️ (Виляет хвостом)"
                        fullness < 30 -> "🐶😢 (Скулит, голодный)"
                        energy < 30 -> "🐶😴 (Зевает, устал)"
                        else -> "🐶😊"
                    },
                    fontSize = 80.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                StatBar("Сытость", fullness, Color.Green)
                StatBar("Счастье", happiness, Color.Yellow)
                StatBar("Энергия", energy, Color.Blue)
            }

            // 2/3 экрана: Взаимодействие
            Column(modifier = Modifier.weight(2f).fillMaxHeight().padding(16.dp)) {
                TabRow(selectedTabIndex = currentTab, containerColor = Color.Transparent) {
                    Tab(selected = currentTab == 0, onClick = { currentTab = 0 }) { Text("🍖 Кормить", fontSize = 24.sp, modifier = Modifier.padding(16.dp)) }
                    Tab(selected = currentTab == 1, onClick = { currentTab = 1 }) { Text("🎾 Играть", fontSize = 24.sp, modifier = Modifier.padding(16.dp)) }
                    Tab(selected = currentTab == 2, onClick = { currentTab = 2 }) { Text("🛁 Уход", fontSize = 24.sp, modifier = Modifier.padding(16.dp)) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                when (currentTab) {
                    0 -> FoodPanel { fullness = (fullness + 20f).coerceAtMost(100f) }
                    1 -> PlayPanel { happiness = (happiness + 20f).coerceAtMost(100f); energy = (energy - 10f).coerceAtLeast(0f) }
                    2 -> CarePanel { energy = (energy + 30f).coerceAtMost(100f) }
                }
            }
        }
    }
}

@Composable
fun FoodPanel(onFeed: () -> Unit) {
    val foods = listOf("🦴 Косточка", "🥩 Мясо", "🥫 Корм", "🌭 Сосиска", "🐟 Рыбка", "🍪 Печенье")
    // Grid layout implementation...
    Column {
        foods.chunked(3).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                rowItems.forEach { food ->
                    Button(onClick = onFeed, modifier = Modifier.size(120.dp, 80.dp).padding(4.dp)) {
                        Text(food, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayPanel(onPlay: () -> Unit) { /* Аналогично FoodPanel, но для игр */ }
@Composable
fun CarePanel(onCare: () -> Unit) { /* Аналогично FoodPanel, но для ухода */ }

@Composable
fun StatBar(name: String, value: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(name, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier.fillMaxWidth().height(16.dp),
            color = color
        )
    }
}
