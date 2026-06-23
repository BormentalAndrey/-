package com.vasilisinaazbuka.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ForestItem(
    val id: String,
    val emoji: String,
    val name: String,
    val category: ItemCategory
)

enum class ItemCategory(val displayName: String, val icon: String) {
    MUSHROOMS("Грибы", "🍄"),
    BERRIES("Ягоды", "🫐"),
    ANIMALS("Животные", "🐿️"),
    FLOWERS("Цветы", "🌸"),
    LEAVES("Листья", "🍂")
}

@Composable
fun ForestSortScreen(onBackClick: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var showInstructions by remember { mutableStateOf(true) }
    
    // Состояние игры
    var foundItems by remember { mutableStateOf(generateForestItems(level)) }
    var collectedItems by remember { mutableStateOf(mapOf<ItemCategory, MutableList<ForestItem>>()) }
    var selectedItem by remember { mutableStateOf<ForestItem?>(null) }
    var showFeedback by remember { mutableStateOf<String?>(null) }
    var isCorrectDrop by remember { mutableStateOf<Boolean?>(null) }
    var gameCompleted by remember { mutableStateOf(false) }
    
    // Анимация для обратной связи
    val feedbackScale by animateFloatAsState(
        targetValue = if (showFeedback != null) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Инициализация корзин
    LaunchedEffect(level) {
        collectedItems = ItemCategory.values().associateWith { mutableListOf<ForestItem>() }
        foundItems = generateForestItems(level)
    }
    
    // Проверка завершения уровня
    LaunchedEffect(collectedItems) {
        val totalCollected = collectedItems.values.sumOf { it.size }
        if (totalCollected == foundItems.size && foundItems.isNotEmpty()) {
            delay(1000)
            if (mistakes == 0) {
                showFeedback = "🌟 Идеально!"
            } else if (mistakes <= 2) {
                showFeedback = "👍 Отлично!"
            } else {
                showFeedback = "😊 Хорошо!"
            }
            gameCompleted = true
        }
    }

    // Функция проверки правильности сортировки
    fun checkSorting(item: ForestItem, targetCategory: ItemCategory): Boolean {
        return item.category == targetCategory
    }
    
    // Функция перемещения предмета в корзину
    fun moveToBasket(item: ForestItem, targetCategory: ItemCategory) {
        if (checkSorting(item, targetCategory)) {
            // Правильно
            score += 10
            collectedItems = collectedItems.toMutableMap().also { map ->
                map[targetCategory] = (map[targetCategory]?.toMutableList() ?: mutableListOf()).also {
                    it.add(item)
                }
            }
            foundItems = foundItems - item
            selectedItem = null
            isCorrectDrop = true
            showFeedback = "✅ Правильно!"
        } else {
            // Неправильно
            mistakes++
            score = (score - 5).coerceAtLeast(0)
            isCorrectDrop = false
            showFeedback = "❌ Не подходит! ${item.emoji} относится к категории «${item.category.displayName}»"
        }
        
        // Сброс анимации через 2 секунды
        LaunchedEffect(showFeedback) {
            delay(2000)
            showFeedback = null
            isCorrectDrop = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF4CAF50),
                        Color(0xFF81C784)
                    )
                )
            )
    ) {
        // Инструкция при первом запуске
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                title = { Text("🌲 Лесная сортировка", fontWeight = FontWeight.Bold) },
                text = { 
                    Text("Помоги собрать лесные находки!\n\n" +
                         "• Нажимай на предметы вверху\n" +
                         "• Выбирай правильную корзину\n" +
                         "• Сортируй грибы, ягоды и животных\n\n" +
                         "Получай очки за правильные ответы!")
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Начать игру!")
                    }
                }
            )
        }
        
        // Кнопка Назад
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(100f)
                .padding(16.dp)
                .size(48.dp)
                .shadow(4.dp, CircleShape)
                .background(Color.White.copy(alpha = 0.9f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Назад",
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF2E7D32)
            )
        }
        
        // Счет и уровень (верхняя панель)
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 80.dp, end = 80.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreItem("⭐", "$score", Color(0xFFFFA000))
                ScoreItem("🎯", "Ур.$level", Color(0xFF1976D2))
                ScoreItem("❌", "$mistakes", Color(0xFFD32F2F))
                
                // Прогресс
                val progress = if (foundItems.isNotEmpty()) {
                    (collectedItems.values.sumOf { it.size }.toFloat() / 
                     (collectedItems.values.sumOf { it.size } + foundItems.size).toFloat())
                } else 0f
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
                ) {
                    Text(
                        "📊", 
                        fontSize = 16.sp
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFFE0E0E0),
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок
            Text(
                text = "🌲 Собери находки в лесу! 🌲",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .shadow(4.dp)
            )
            
            // Обратная связь
            if (showFeedback != null) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .scale(feedbackScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrectDrop == true) 
                            Color(0xFFC8E6C9) 
                        else if (isCorrectDrop == false) 
                            Color(0xFFFFCDD2) 
                        else 
                            Color(0xFFFFF9C4)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = showFeedback ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrectDrop == true) Color(0xFF2E7D32)
                               else if (isCorrectDrop == false) Color(0xFFC62828)
                               else Color(0xFFF57F17),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Найденные предметы (сетка)
            if (foundItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Найдено в лесу:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(foundItems) { item ->
                                ForestItemCard(
                                    item = item,
                                    isSelected = selectedItem == item,
                                    onClick = {
                                        selectedItem = if (selectedItem == item) null else item
                                    }
                                )
                            }
                        }
                    }
                }
            } else if (gameCompleted) {
                // Экран завершения уровня
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🎉", fontSize = 64.sp)
                        Text(
                            "Уровень $level пройден!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            "Счёт: $score очков",
                            fontSize = 20.sp,
                            color = Color(0xFF1976D2)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                level++
                                gameCompleted = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Следующий уровень →", fontSize = 18.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Корзины для сортировки
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Корзины:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (level >= 3) 3 else 2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = when {
                            level >= 5 -> ItemCategory.values().toList()
                            level >= 3 -> listOf(ItemCategory.MUSHROOMS, ItemCategory.BERRIES, ItemCategory.ANIMALS)
                            else -> listOf(ItemCategory.MUSHROOMS, ItemCategory.BERRIES)
                        }
                        
                        items(categories) { category ->
                            Basket(
                                category = category,
                                items = collectedItems[category] ?: emptyList(),
                                isHighlighted = selectedItem?.category == category,
                                onClick = {
                                    selectedItem?.let { item ->
                                        moveToBasket(item, category)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForestItemCard(
    item: ForestItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, Color(0xFFFFA000), RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF8E1) else Color(0xFFF5F5F5)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 40.sp
                )
                Text(
                    text = item.name,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun Basket(
    category: ItemCategory,
    items: List<ForestItem>,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                enabled = isHighlighted,
                onClick = onClick
            )
            .then(
                if (isHighlighted) Modifier.border(3.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlighted) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isHighlighted -> Color(0xFFE8F5E9)
                items.isNotEmpty() -> Color(0xFFFFF8E1)
                else -> Color(0xFFF5F5F5)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.icon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = category.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center
                )
            }
            
            if (items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.take(3).forEach { item ->
                        Text(
                            text = item.emoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                    if (items.size > 3) {
                        Text(
                            text = "+${items.size - 3}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            if (isHighlighted) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = "Положить сюда",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ScoreItem(emoji: String, text: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp
        )
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Генерация лесных предметов для уровня
fun generateForestItems(level: Int): List<ForestItem> {
    val items = mutableListOf<ForestItem>()
    val baseItems = listOf(
        ForestItem("1", "🍄", "Белый гриб", ItemCategory.MUSHROOMS),
        ForestItem("2", "🍄‍🟫", "Подберёзовик", ItemCategory.MUSHROOMS),
        ForestItem("3", "🟤", "Моховик", ItemCategory.MUSHROOMS),
        ForestItem("4", "🍄", "Лисичка", ItemCategory.MUSHROOMS),
        ForestItem("5", "🫐", "Черника", ItemCategory.BERRIES),
        ForestItem("6", "🍓", "Земляника", ItemCategory.BERRIES),
        ForestItem("7", "🫐", "Голубика", ItemCategory.BERRIES),
        ForestItem("8", "🍒", "Вишня", ItemCategory.BERRIES),
        ForestItem("9", "🐿️", "Белка", ItemCategory.ANIMALS),
        ForestItem("10", "🦔", "Ёжик", ItemCategory.ANIMALS),
        ForestItem("11", "🐰", "Заяц", ItemCategory.ANIMALS),
        ForestItem("12", "🦊", "Лиса", ItemCategory.ANIMALS),
        ForestItem("13", "🌸", "Ромашка", ItemCategory.FLOWERS),
        ForestItem("14", "🌻", "Подсолнух", ItemCategory.FLOWERS),
        ForestItem("15", "🌺", "Иван-чай", ItemCategory.FLOWERS),
        ForestItem("16", "🍂", "Клён", ItemCategory.LEAVES),
        ForestItem("17", "🍁", "Дуб", ItemCategory.LEAVES),
        ForestItem("18", "🌿", "Папоротник", ItemCategory.LEAVES)
    )
    
    val itemCount = when {
        level <= 2 -> 6
        level <= 4 -> 9
        else -> 12
    }
    
    val categoriesToUse = when {
        level >= 5 -> ItemCategory.values().toList()
        level >= 3 -> listOf(ItemCategory.MUSHROOMS, ItemCategory.BERRIES, ItemCategory.ANIMALS)
        else -> listOf(ItemCategory.MUSHROOMS, ItemCategory.BERRIES)
    }
    
    val availableItems = baseItems.filter { it.category in categoriesToUse }
    
    return availableItems.shuffled().take(itemCount)
}
