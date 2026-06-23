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

enum class Screen {
    MENU,
    BIM_EMOTIONS,
    FEED_BIM,
    FOREST_SORT,
    HAPPY_COUNTING,
    KUZYA_CLOCK,
    KUZYA_GARAGE,
    RUSSIA_MAP
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
        Screen.RUSSIA_MAP -> RussiaMapScreen(onBackClick = { currentScreen = Screen.MENU })
    }
}

@Composable
fun MainMenu(onScreenSelected: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF81C784),
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎮 Весёлые приключения\nКузи и Бима!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Выбери игру:",
            fontSize = 24.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Список игр
        val games = listOf(
            Triple("🐶😊", "Эмоции Бима", Screen.BIM_EMOTIONS),
            Triple("🍖", "Покорми Бима", Screen.FEED_BIM),
            Triple("🌲", "Лесная сортировка", Screen.FOREST_SORT),
            Triple("🔢", "Весёлый счёт", Screen.HAPPY_COUNTING),
            Triple("🕐", "Часы Кузи", Screen.KUZYA_CLOCK),
            Triple("🚗", "Гараж Кузи", Screen.KUZYA_GARAGE),
            Triple("🗺️", "Карта России", Screen.RUSSIA_MAP)
        )
        
        games.forEach { (emoji, name, screen) ->
            Button(
                onClick = { onScreenSelected(screen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
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
