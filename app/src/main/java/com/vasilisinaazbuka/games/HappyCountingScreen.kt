package com.vasilisinaazbuka.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.random.Random

data class CountingLevel(
    val level: Int,
    val minCount: Int,
    val maxCount: Int,
    val objects: List<String>,
    val description: String
)

@Composable
fun HappyCountingScreen(onBackClick: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    var totalAttempts by remember { mutableIntStateOf(0) }
    var showInstructions by remember { mutableStateOf(true) }
    
    val levels = remember {
        listOf(
            CountingLevel(1, 1, 3, listOf("🎾"), "Мячики"),
            CountingLevel(2, 1, 6, listOf("🎾"), "Мячики"),
            CountingLevel(3, 2, 8, listOf("🍖", "🦴"), "Угощения"),
            CountingLevel(4, 1, 10, listOf("🐿️", "🐰", "🦊"), "Зверята"),
            CountingLevel(5, 3, 12, listOf("🌸", "🌻", "🍄", "🫐"), "Лесные находки")
        )
    }
    
    val currentLevel = levels.getOrElse(level - 1) { levels.last() }
    
    var targetCount by remember { mutableIntStateOf(Random.nextInt(currentLevel.minCount, currentLevel.maxCount + 1)) }
    var selectedObject by remember { mutableStateOf(currentLevel.objects.random()) }
    var options by remember { mutableStateOf(generateOptions(targetCount, currentLevel.maxCount)) }
    
    var bimEmotion by remember { mutableIntStateOf(R.drawable.bim_neutral) }
    var kuzyaEmotion by remember { mutableIntStateOf(R.drawable.kuzya_normal) }
    var showFeedback by remember { mutableStateOf("") }
    var isCorrectAnswer by remember { mutableStateOf<Boolean?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    
    val feedbackScale by animateFloatAsState(
        targetValue = if (showFeedback.isNotEmpty()) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val bimScale by animateFloatAsState(
        targetValue = when {
            showCelebration -> 1.3f
            isCorrectAnswer == false -> 0.9f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val kuzyaBounce by animateFloatAsState(
        targetValue = if (showCelebration) 1.2f else 1f,
        animationSpec = repeatable(
            iterations = if (showCelebration) 3 else 1,
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    fun generateNewRound(levelConfig: CountingLevel) {
        targetCount = Random.nextInt(levelConfig.minCount, levelConfig.maxCount + 1)
        selectedObject = levelConfig.objects.random()
        options = generateOptions(targetCount, levelConfig.maxCount)
        bimEmotion = R.drawable.bim_neutral
        kuzyaEmotion = R.drawable.kuzya_normal
    }
    
    LaunchedEffect(showFeedback) {
        if (showFeedback.isNotEmpty()) {
            delay(2000)
            showFeedback = ""
            if (isCorrectAnswer == true) {
                showCelebration = false
                generateNewRound(currentLevel)
            }
            isCorrectAnswer = null
            selectedOption = null
        }
    }
    
    LaunchedEffect(correctAnswers) {
        if (correctAnswers >= 3 * level && level < levels.size && isCorrectAnswer == true) {
            delay(1500)
            level++
            correctAnswers = 0
            bimEmotion = R.drawable.bim_happy
            kuzyaEmotion = R.drawable.kuzya_celebrating
            showFeedback = "🎊 Новый уровень! ${levels.getOrElse(level - 1) { levels.last() }.description}"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Фоновое изображение
        Image(
            painter = painterResource(id = R.drawable.background_counting),
            contentDescription = "Фон счёта",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Полупрозрачный слой
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x30000000))
        )
        
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                title = { 
                    Text("🎯 Весёлый счёт", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                },
                text = {
                    Text(
                        "Помоги Кузе и Биму считать!\n\n" +
                        "• Посчитай предметы на экране\n" +
                        "• Выбери правильное число\n" +
                        "• С каждым уровнем сложнее\n\n" +
                        "🌈 Собери 3 правильных ответа для нового уровня!",
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Начинаем! 🚀", fontSize = 18.sp)
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
        
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
                tint = Color(0xFF1565C0)
            )
        }
        
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
                StatChip("⭐", "$score", Color(0xFFFFA000))
                StatChip("🎯", "Ур.$level", Color(0xFF1976D2))
                StatChip("✅", "$correctAnswers/3", Color(0xFF4CAF50))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(kuzyaBounce),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = kuzyaEmotion),
                            contentDescription = "Кузя",
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Кузя", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(bimScale),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = bimEmotion),
                            contentDescription = "Бим",
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Бим", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Сколько здесь предметов?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center
                        )
                        Text(text = currentLevel.description, fontSize = 16.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(targetCount) { index ->
                                val itemScale by animateFloatAsState(
                                    targetValue = 1f,
                                    animationSpec = if (showCelebration) {
                                        repeatable(
                                            iterations = 2,
                                            animation = tween(500, delayMillis = index * 100),
                                            repeatMode = RepeatMode.Reverse
                                        )
                                    } else {
                                        tween(0)
                                    }
                                )
                                
                                Text(
                                    text = selectedObject,
                                    fontSize = 48.sp,
                                    modifier = Modifier.scale(itemScale)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedVisibility(
                    visible = showFeedback.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(feedbackScale),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isCorrectAnswer == true -> Color(0xFFC8E6C9)
                                isCorrectAnswer == false -> Color(0xFFFFCDD2)
                                else -> Color(0xFFFFF9C4)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = showFeedback,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCorrectAnswer == true -> Color(0xFF2E7D32)
                                isCorrectAnswer == false -> Color(0xFFC62828)
                                else -> Color(0xFFF57F17)
                            },
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option
                        val buttonScale by animateFloatAsState(
                            targetValue = when {
                                isSelected && isCorrectAnswer == true -> 1.1f
                                isSelected && isCorrectAnswer == false -> 0.9f
                                else -> 1f
                            },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        
                        Button(
                            onClick = { 
                                if (selectedOption == null) {
                                    selectedOption = option
                                    totalAttempts++
                                    
                                    if (option == targetCount) {
                                        isCorrectAnswer = true
                                        correctAnswers++
                                        val points = when {
                                            level <= 2 -> 10
                                            level <= 4 -> 20
                                            else -> 30
                                        }
                                        score += points
                                        bimEmotion = listOf(
                                            R.drawable.bim_happy,
                                            R.drawable.bim_love,
                                            R.drawable.bim_play,
                                            R.drawable.bim_happy
                                        ).random()
                                        kuzyaEmotion = listOf(
                                            R.drawable.kuzya_happy,
                                            R.drawable.kuzya_celebrating,
                                            R.drawable.kuzya_happy,
                                            R.drawable.kuzya_celebrating
                                        ).random()
                                        showFeedback = listOf(
                                            "✅ Правильно! +$points",
                                            "🌟 Молодец! +$points",
                                            "🎯 В точку! +$points",
                                            "💫 Отлично! +$points"
                                        ).random()
                                        showCelebration = true
                                    } else {
                                        isCorrectAnswer = false
                                        bimEmotion = listOf(
                                            R.drawable.bim_sad,
                                            R.drawable.bim_scared,
                                            R.drawable.bim_surprised
                                        ).random()
                                        kuzyaEmotion = R.drawable.kuzya_thinking
                                        showFeedback = listOf(
                                            "❌ Не совсем! Попробуй ещё",
                                            "🤔 Подумай ещё разок",
                                            "💭 Посчитай внимательнее"
                                        ).random()
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(100.dp)
                                .scale(buttonScale)
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            enabled = selectedOption == null || isSelected,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isSelected && isCorrectAnswer == true -> Color(0xFF4CAF50)
                                    isSelected && isCorrectAnswer == false -> Color(0xFFF44336)
                                    isSelected -> Color(0xFFFFA000)
                                    else -> Color(0xFF1976D2)
                                },
                                disabledContainerColor = when {
                                    isSelected && isCorrectAnswer == true -> Color(0xFF4CAF50)
                                    isSelected && isCorrectAnswer == false -> Color(0xFFF44336)
                                    else -> Color(0xFFBBDEFB)
                                }
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Text(
                                text = option.toString(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                if (totalAttempts >= 3 && isCorrectAnswer == null) {
                    Text(
                        text = "💡 Подсказка: попробуй посчитать пальчиком каждый предмет",
                        fontSize = 14.sp,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatChip(emoji: String, text: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

fun generateOptions(correct: Int, maxValue: Int): List<Int> {
    val options = mutableSetOf(correct)
    val range = when {
        maxValue <= 5 -> 2
        maxValue <= 10 -> 3
        else -> 4
    }
    
    while (options.size < 3) {
        val wrong = correct + Random.nextInt(-range, range + 1)
        if (wrong > 0 && wrong <= maxValue && wrong != correct) {
            options.add(wrong)
        }
    }
    
    return options.shuffled()
}
