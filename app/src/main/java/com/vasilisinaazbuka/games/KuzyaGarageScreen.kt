package com.vasilisinaazbuka.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class Car(
    val id: String,
    val name: String,
    val emoji: String,
    val color: CarColor,
    val type: CarType,
    val description: String
)

enum class CarColor(val displayName: String, val color: Color, val lightColor: Color) {
    RED("Красный", Color(0xFFD32F2F), Color(0xFFFFCDD2)),
    BLUE("Синий", Color(0xFF1976D2), Color(0xFFBBDEFB)),
    YELLOW("Желтый", Color(0xFFFBC02D), Color(0xFFFFF9C4)),
    GREEN("Зелёный", Color(0xFF388E3C), Color(0xFFC8E6C9)),
    ORANGE("Оранжевый", Color(0xFFF57C00), Color(0xFFFFE0B2)),
    PURPLE("Фиолетовый", Color(0xFF7B1FA2), Color(0xFFE1BEE7))
}

enum class CarType(val displayName: String, val emoji: String) {
    SEDAN("Легковая", "🚗"),
    SUV("Внедорожник", "🚙"),
    TAXI("Такси", "🚕"),
    SPORT("Спортивная", "🏎️"),
    TRUCK("Грузовик", "🚚"),
    POLICE("Полиция", "🚓"),
    AMBULANCE("Скорая", "🚑"),
    FIRE("Пожарная", "🚒"),
    BUS("Автобус", "🚌"),
    MINIBUS("Микроавтобус", "🚐")
}

@Composable
fun KuzyaGarageScreen(onBackClick: () -> Unit) {
    // Состояния игры
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var correctPlacements by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var showInstructions by remember { mutableStateOf(true) }
    
    // Генерация машин и гаражей
    var availableCars by remember { mutableStateOf(generateCars(level)) }
    var garages by remember { mutableStateOf(generateGarages(level)) }
    var parkedCars by remember { mutableStateOf(mapOf<CarColor, MutableList<Car>>()) }
    
    // Текущие состояния
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    var showFeedback by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    
    // Анимации
    val feedbackScale by animateFloatAsState(
        targetValue = if (showFeedback.isNotEmpty()) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val carScale by animateFloatAsState(
        targetValue = if (selectedCar != null) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Инициализация
    LaunchedEffect(level) {
        availableCars = generateCars(level)
        garages = generateGarages(level)
        parkedCars = garages.associate { it to mutableListOf<Car>() }
    }
    
    // Проверка завершения уровня
    LaunchedEffect(parkedCars) {
        val totalParked = parkedCars.values.sumOf { it.size }
        if (totalParked == availableCars.size && availableCars.isNotEmpty()) {
            delay(1000)
            showCelebration = true
            showFeedback = when {
                mistakes == 0 -> "🌟 Идеально! Все машинки на местах!"
                mistakes <= 2 -> "👍 Отлично! Почти без ошибок!"
                else -> "😊 Хорошо! Продолжай учиться!"
            }
            correctPlacements = 0
            
            delay(2000)
            if (level < 6) {
                level++
                showCelebration = false
                showFeedback = ""
            }
        }
    }
    
    // Функция парковки машины
    fun parkCar(car: Car, targetColor: CarColor) {
        if (car.color == targetColor) {
            // Правильная парковка
            isCorrect = true
            correctPlacements++
            score += 10 * level
            
            parkedCars = parkedCars.toMutableMap().also { map ->
                map[targetColor] = (map[targetColor]?.toMutableList() ?: mutableListOf()).also {
                    it.add(car)
                }
            }
            availableCars = availableCars - car
            selectedCar = null
            
            showFeedback = listOf(
                "✅ Правильно! ${car.name} в ${targetColor.displayName} гараже!",
                "🎯 Точно в цвет! +${10 * level} очков",
                "👏 Отлично! ${car.emoji} на месте!"
            ).random()
        } else {
            // Неправильная парковка
            isCorrect = false
            mistakes++
            score = (score - 5).coerceAtLeast(0)
            selectedCar = null
            
            showFeedback = "❌ Ой! ${car.name} должна быть в ${car.color.displayName} гараже!"
        }
        
        // Сброс обратной связи
        LaunchedEffect(showFeedback) {
            delay(2500)
            showFeedback = ""
            isCorrect = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFECEFF1),
                        Color(0xFFCFD8DC),
                        Color(0xFFB0BEC5)
                    )
                )
            )
    ) {
        // Инструкция
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                title = {
                    Text("🚗 Гараж Кузи", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                },
                text = {
                    Text(
                        "Помоги Кузе расставить машинки по цветам!\n\n" +
                        "🎯 Как играть:\n" +
                        "1. Выбери машинку внизу\n" +
                        "2. Нажми на гараж нужного цвета\n" +
                        "3. Собери все машинки правильно\n\n" +
                        "🌈 Чем выше уровень, тем больше машинок и цветов!\n" +
                        "⭐ Идеальная парковка без ошибок даёт бонус!",
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Поехали! 🚀", fontSize = 18.sp)
                    }
                },
                shape = RoundedCornerShape(20.dp)
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
                tint = Color(0xFF424242)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя панель статистики
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem("⭐", "$score", Color(0xFFFFA000))
                    StatItem("🎯", "Ур.$level", Color(0xFF1976D2))
                    StatItem("✅", "$correctPlacements/${availableCars.size + (parkedCars.values.sumOf { it.size })}", Color(0xFF4CAF50))
                    StatItem("❌", "$mistakes", Color(0xFFD32F2F))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Заголовок
            Text(
                text = "🚗 Расставь машинки по цветам!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Обратная связь
            AnimatedVisibility(
                visible = showFeedback.isNotEmpty(),
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .scale(feedbackScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isCorrect == true -> Color(0xFFC8E6C9)
                            isCorrect == false -> Color(0xFFFFCDD2)
                            else -> Color(0xFFFFF9C4)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = showFeedback,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCorrect == true -> Color(0xFF2E7D32)
                            isCorrect == false -> Color(0xFFC62828)
                            else -> Color(0xFFF57F17)
                        },
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Гаражи
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(garages) { garageColor ->
                    GarageBox(
                        color = garageColor,
                        cars = parkedCars[garageColor] ?: emptyList(),
                        isHighlighted = selectedCar != null,
                        isCorrectColor = selectedCar?.color == garageColor,
                        onClick = {
                            selectedCar?.let { car ->
                                parkCar(car, garageColor)
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Индикатор выбранной машинки
            if (selectedCar != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(feedbackScale),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = selectedCar!!.color.lightColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Выбрана: ${selectedCar!!.emoji}",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedCar!!.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedCar!!.color.color
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Выбери гараж",
                            tint = selectedCar!!.color.color
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Доступные машинки
            if (availableCars.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Выбери машинку:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(availableCars) { car ->
                                CarCard(
                                    car = car,
                                    isSelected = selectedCar == car,
                                    onClick = {
                                        selectedCar = if (selectedCar == car) null else car
                                    },
                                    scale = if (selectedCar == car) 1.1f else 1f
                                )
                            }
                        }
                    }
                }
            } else if (showCelebration) {
                // Экран победы
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🎉", fontSize = 64.sp)
                        Text(
                            text = "Уровень $level пройден!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Счёт: $score очков",
                            fontSize = 20.sp,
                            color = Color(0xFF1976D2)
                        )
                        
                        if (mistakes == 0) {
                            Text(
                                text = "🌟 Безупречно! Ни одной ошибки!",
                                fontSize = 18.sp,
                                color = Color(0xFFFFA000),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    level = 1
                                    score = 0
                                    mistakes = 0
                                    correctPlacements = 0
                                    showCelebration = false
                                    showFeedback = ""
                                }
                            ) {
                                Text("🔄 Заново")
                            }
                            
                            Button(
                                onClick = {
                                    if (level < 6) {
                                        level++
                                        showCelebration = false
                                        showFeedback = ""
                                    }
                                },
                                enabled = level < 6,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Text("▶️ Далее")
                            }
                        }
                    }
                }
            }
            
            // Подсказка
            if (selectedCar == null && availableCars.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "👆 Нажми на машинку, чтобы выбрать её",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun GarageBox(
    color: CarColor,
    cars: List<Car>,
    isHighlighted: Boolean,
    isCorrectColor: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isHighlighted && isCorrectColor -> 1.05f
            isHighlighted -> 0.95f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .scale(scale)
            .clickable(enabled = isHighlighted, onClick = onClick)
            .then(
                if (isHighlighted && isCorrectColor) {
                    Modifier.border(3.dp, color.color, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlighted && isCorrectColor) 12.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = color.lightColor.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Крыша гаража
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(color.color.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏠",
                    fontSize = 24.sp
                )
            }
            
            // Название гаража
            Text(
                text = color.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color.color,
                textAlign = TextAlign.Center
            )
            
            // Припаркованные машинки
            if (cars.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cars.take(3).forEach { car ->
                        Text(
                            text = car.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(1.dp)
                        )
                    }
                    if (cars.size > 3) {
                        Text(
                            text = "+${cars.size - 3}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Text(
                    text = "Пусто",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // Индикатор правильности
            if (isHighlighted && isCorrectColor) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Парковать здесь",
                    tint = color.color,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CarCard(
    car: Car,
    isSelected: Boolean,
    onClick: () -> Unit,
    scale: Float
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .scale(scale)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier
                        .border(3.dp, car.color.color, RoundedCornerShape(12.dp))
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                } else {
                    Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) car.color.lightColor else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = car.emoji,
                fontSize = 48.sp
            )
            Text(
                text = car.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = car.color.color,
                textAlign = TextAlign.Center
            )
            Text(
                text = car.type.displayName,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Выбрана",
                    tint = car.color.color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StatItem(emoji: String, text: String, color: Color) {
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

// Генерация машин для уровня
fun generateCars(level: Int): List<Car> {
    val allCars = listOf(
        Car("1", "Красная", "🚗", CarColor.RED, CarType.SEDAN, "Быстрая красная машина"),
        Car("2", "Синяя", "🚙", CarColor.BLUE, CarType.SUV, "Вместительный внедорожник"),
        Car("3", "Желтая", "🚕", CarColor.YELLOW, CarType.TAXI, "Городское такси"),
        Car("4", "Зелёная", "🏎️", CarColor.GREEN, CarType.SPORT, "Спортивный автомобиль"),
        Car("5", "Оранжевая", "🚚", CarColor.ORANGE, CarType.TRUCK, "Большой грузовик"),
        Car("6", "Фиолетовая", "🚓", CarColor.PURPLE, CarType.POLICE, "Полицейская машина"),
        Car("7", "Белая", "🚑", CarColor.RED, CarType.AMBULANCE, "Скорая помощь"),
        Car("8", "Красная-2", "🚒", CarColor.RED, CarType.FIRE, "Пожарная машина"),
        Car("9", "Синяя-2", "🚌", CarColor.BLUE, CarType.BUS, "Школьный автобус"),
        Car("10", "Желтая-2", "🚐", CarColor.YELLOW, CarType.MINIBUS, "Микроавтобус")
    )
    
    val carsPerLevel = when (level) {
        1 -> 3
        2 -> 4
        3 -> 6
        4 -> 8
        5 -> 9
        else -> 10
    }
    
    val colorsPerLevel = when (level) {
        1 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW)
        2 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW, CarColor.GREEN)
        3 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW, CarColor.GREEN, CarColor.ORANGE)
        else -> CarColor.values().toList()
    }
    
    val availableCars = allCars.filter { it.color in colorsPerLevel }
    return availableCars.shuffled().take(carsPerLevel)
}

// Генерация гаражей для уровня
fun generateGarages(level: Int): List<CarColor> {
    return when (level) {
        1 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW)
        2 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW, CarColor.GREEN)
        3 -> listOf(CarColor.RED, CarColor.BLUE, CarColor.YELLOW, CarColor.GREEN, CarColor.ORANGE)
        else -> CarColor.values().toList()
    }
}
