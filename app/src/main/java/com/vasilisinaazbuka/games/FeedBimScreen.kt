package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FeedBimScreen(onBackClick: () -> Unit) {
    var fullness by remember { mutableFloatStateOf(50f) }
    var happiness by remember { mutableFloatStateOf(50f) }
    var energy by remember { mutableFloatStateOf(50f) }
    var currentTab by remember { mutableIntStateOf(0) }
    
    // Состояние для анимаций и обратной связи
    var showReaction by remember { mutableStateOf("") }
    var showHearts by remember { mutableStateOf(false) }
    
    // Автоматическое уменьшение характеристик со временем
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Каждые 5 секунд
            fullness = (fullness - 1f).coerceAtLeast(0f)
            happiness = (happiness - 0.5f).coerceAtLeast(0f)
            energy = (energy - 0.5f).coerceAtLeast(0f)
        }
    }
    
    // Автоматическое скрытие реакции
    LaunchedEffect(showReaction) {
        if (showReaction.isNotEmpty()) {
            delay(2000)
            showReaction = ""
            showHearts = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        Color(0xFFA5D6A7)
                    )
                )
            )
    ) {
        // Кнопка Назад
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(100f)
                .padding(16.dp)
                .size(48.dp)
                .border(2.dp, Color.White, CircleShape)
                .background(Color.White.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close, 
                contentDescription = "Назад", 
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF2E7D32)
            )
        }

        // Реакция Бима (всплывающая эмоция)
        if (showReaction.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 40.dp, top = 100.dp)
                    .zIndex(200f)
            ) {
                Text(
                    text = showReaction,
                    fontSize = 60.sp
                )
            }
        }
        
        // Анимация сердечек
        if (showHearts) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(200f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red.copy(alpha = 0.8f - (index * 0.1f)),
                            modifier = Modifier.size(40.dp - (index * 4).dp)
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // 1/3 экрана: Персонаж Бим и его статы
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Анимированный Бим
                Card(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(8.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                fullness > 80 && happiness > 80 -> "🐶❤️"
                                fullness < 30 -> "🐶😢"
                                energy < 30 -> "🐶😴"
                                happiness > 70 -> "🐶😊"
                                else -> "🐶😐"
                            },
                            fontSize = 80.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Имя и статус
                Text(
                    text = "Бим",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                
                Text(
                    text = when {
                        fullness > 80 && happiness > 80 -> "Счастлив!"
                        fullness < 30 -> "Голодный..."
                        energy < 30 -> "Устал..."
                        happiness < 30 -> "Грустный..."
                        else -> "Нормально"
                    },
                    fontSize = 16.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Статистики
                StatBarEnhanced("🍖 Сытость", fullness, Color(0xFF4CAF50), Color(0xFF2E7D32))
                StatBarEnhanced("😊 Счастье", happiness, Color(0xFFFFC107), Color(0xFFFF8F00))
                StatBarEnhanced("⚡ Энергия", energy, Color(0xFF2196F3), Color(0xFF1565C0))
            }

            // 2/3 экрана: Взаимодействие
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                // Табы
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    TabRow(
                        selectedTabIndex = currentTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF2E7D32),
                        divider = {}
                    ) {
                        Tab(
                            selected = currentTab == 0, 
                            onClick = { currentTab = 0 }
                        ) { 
                            Text("🍖 Кормить", fontSize = 18.sp, modifier = Modifier.padding(12.dp)) 
                        }
                        Tab(
                            selected = currentTab == 1, 
                            onClick = { currentTab = 1 }
                        ) { 
                            Text("🎾 Играть", fontSize = 18.sp, modifier = Modifier.padding(12.dp)) 
                        }
                        Tab(
                            selected = currentTab == 2, 
                            onClick = { currentTab = 2 }
                        ) { 
                            Text("🛁 Уход", fontSize = 18.sp, modifier = Modifier.padding(12.dp)) 
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Контент вкладок
                when (currentTab) {
                    0 -> FoodPanel(
                        onFeed = { food ->
                            fullness = (fullness + 20f).coerceAtMost(100f)
                            happiness = (happiness + 5f).coerceAtMost(100f)
                            showReaction = "😋 Вкусно!"
                            showHearts = true
                        },
                        onOverfeed = {
                            fullness = (fullness + 10f).coerceAtMost(100f)
                            showReaction = "😖 Больше не могу..."
                        }
                    )
                    1 -> PlayPanel(
                        onPlay = { game ->
                            happiness = (happiness + 20f).coerceAtMost(100f)
                            energy = (energy - 10f).coerceAtLeast(0f)
                            showReaction = "🎉 Весело!"
                            showHearts = true
                        }
                    )
                    2 -> CarePanel(
                        onCare = { careType ->
                            when (careType) {
                                "sleep" -> {
                                    energy = (energy + 30f).coerceAtMost(100f)
                                    showReaction = "😴 Поспал..."
                                }
                                "wash" -> {
                                    energy = (energy + 15f).coerceAtMost(100f)
                                    happiness = (happiness + 10f).coerceAtMost(100f)
                                    showReaction = "🛁 Чистый!"
                                }
                                "heal" -> {
                                    energy = (energy + 20f).coerceAtMost(100f)
                                    fullness = (fullness - 5f).coerceAtLeast(0f)
                                    showReaction = "💊 Полегчало!"
                                }
                            }
                            showHearts = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodPanel(onFeed: (String) -> Unit, onOverfeed: () -> Unit) {
    val foods = listOf(
        "🦴" to "Косточка",
        "🥩" to "Мясо",
        "🥫" to "Корм",
        "🌭" to "Сосиска",
        "🐟" to "Рыбка",
        "🍪" to "Печенье"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выбери еду для Бима:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Сетка с едой
            foods.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { (emoji, name) ->
                        Button(
                            onClick = { onFeed(name) },
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8F5E9)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(emoji, fontSize = 32.sp)
                                Text(
                                    name, 
                                    fontSize = 14.sp, 
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            // Кнопка перекорма (пасхалка)
            TextButton(
                onClick = onOverfeed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Дать ещё (но Бим может не захотеть)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PlayPanel(onPlay: (String) -> Unit) {
    val games = listOf(
        "🎾" to "Мячик",
        "🦴" to "Апорт",
        "🏃" to "Бегать",
        "🪢" to "Канатик",
        "🫧" to "Пузыри",
        "💃" to "Танцы"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Поиграй с Бимом:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            games.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { (emoji, name) ->
                        Button(
                            onClick = { onPlay(name) },
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFF3E0)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(emoji, fontSize = 32.sp)
                                Text(
                                    name, 
                                    fontSize = 14.sp, 
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarePanel(onCare: (String) -> Unit) {
    val careActions = listOf(
        "😴" to "Сон" to "sleep",
        "🛁" to "Мытьё" to "wash",
        "💊" to "Лечить" to "heal",
        "🪥" to "Чистить" to "groom",
        "🏥" to "Осмотр" to "checkup",
        "💆" to "Массаж" to "massage"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Позаботься о Биме:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            careActions.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { (pair, action) ->
                        val (emoji, name) = pair
                        Button(
                            onClick = { onCare(action) },
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE3F2FD)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(emoji, fontSize = 32.sp)
                                Text(
                                    name, 
                                    fontSize = 14.sp, 
                                    color = Color(0xFF1565C0),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBarEnhanced(name: String, value: Float, color: Color, trackColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                fontSize = 14.sp
            )
            Text(
                text = "${value.roundToInt()}%",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = trackColor.copy(alpha = 0.2f),
        )
    }
}
