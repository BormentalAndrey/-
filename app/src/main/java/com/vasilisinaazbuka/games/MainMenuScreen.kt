package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen() {
    var currentScreen by remember { mutableStateOf(Screen.MENU) }
    
    when (currentScreen) {
        Screen.MENU -> MainMenu(onScreenSelected = { currentScreen = it })
        Screen.BIM_EMOTIONS -> BimEmotionsScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.FEED_BIM -> FeedBimScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.FOREST_SORT -> ForestSortScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.HAPPY_COUNTING -> HappyCountingScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.KUZYA_CLOCK -> KuzyaClockScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.KUZYA_GARAGE -> KuzyaGarageScreen(onBackClick = { currentScreen = Screen.MENU })
        Screen.RUSSIA_MAP -> RussiaMapScreen(onBackClick = { currentScreen = Screen.MENU })
    }
}

@Composable
fun MainMenu(onScreenSelected: (Screen) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(  // ГОРИЗОНТАЛЬНЫЙ градиент вместо вертикального
                    colors = listOf(
                        Color(0xFF81C784),
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32)
                    )
                )
            )
    ) {
        Row(  // ГОРИЗОНТАЛЬНОЕ расположение
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть - заголовок и персонажи
            Column(
                modifier = Modifier.weight(0.35f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎮 Весёлые\nприключения\nКузи и Бима!",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Персонажи
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text("👦", fontSize = 80.sp)
                    Text("🐶", fontSize = 80.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Кузя и Бим",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Правая часть - кнопки игр
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(start = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Выбери игру:",
                    fontSize = 28.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Сетка кнопок 2x4 (для 7 игр + 1 пустая)
                val games = listOf(
                    Triple("🐶😊", "Эмоции Бима", Screen.BIM_EMOTIONS),
                    Triple("🍖", "Покорми Бима", Screen.FEED_BIM),
                    Triple("🌲", "Лесная сортировка", Screen.FOREST_SORT),
                    Triple("🔢", "Весёлый счёт", Screen.HAPPY_COUNTING),
                    Triple("🕐", "Часы Кузи", Screen.KUZYA_CLOCK),
                    Triple("🚗", "Гараж Кузи", Screen.KUZYA_GARAGE),
                    Triple("🗺️", "Карта России", Screen.RUSSIA_MAP)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    games.chunked(2).forEach { rowGames ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowGames.forEach { (emoji, name, screen) ->
                                Button(
                                    onClick = { onScreenSelected(screen) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(80.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.9f)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 6.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(emoji, fontSize = 28.sp)
                                        Text(
                                            name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                            // Добавляем пустое место, если в ряду 1 элемент
                            if (rowGames.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "🌟 Играй и учись вместе с Кузей и Бимом! 🌟",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
