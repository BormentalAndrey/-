package com.vasilisinaazbuka.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen {
    MENU,
    BIM_EMOTIONS,
    FEED_BIM,
    FOREST_SORT,
    HAPPY_COUNTING,
    KUZYA_CLOCK,
    KUZYA_GARAGE
}

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
    }
}

@Composable
fun MainMenu(onScreenSelected: (Screen) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.background_main),
            contentDescription = "Фон главного меню",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Полупрозрачный слой для читаемости текста
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x40000000))
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть - заголовок и персонажи
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "🎮 Весёлые\nприключения\nКузи и Бима!",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kuzya_normal),
                        contentDescription = "Кузя",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                    Image(
                        painter = painterResource(id = R.drawable.bim_happy),
                        contentDescription = "Бим",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Кузя и Бим",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Правая часть - кнопки игр
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .padding(start = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Выбери игру:",
                    fontSize = 28.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val games = listOf(
                    Triple("🐶😊", "Эмоции Бима", Screen.BIM_EMOTIONS),
                    Triple("🍖", "Покорми Бима", Screen.FEED_BIM),
                    Triple("🌲", "Лесная сортировка", Screen.FOREST_SORT),
                    Triple("🔢", "Весёлый счёт", Screen.HAPPY_COUNTING),
                    Triple("🕐", "Часы Кузи", Screen.KUZYA_CLOCK),
                    Triple("🚗", "Гараж Кузи", Screen.KUZYA_GARAGE)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    games.chunked(2).forEach { rowGames: List<Triple<String, String, Screen>> ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowGames.forEach { (emoji: String, name: String, screen: Screen) ->
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
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
