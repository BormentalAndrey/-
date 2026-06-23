package com.vasilisinaazbuka.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.*

data class ScheduleItem(
    val time: String,
    val activity: String,
    val emoji: String,
    val hour: Int,
    val minute: Int
)

@Composable
fun KuzyaClockScreen(onBackClick: () -> Unit) {
    val fullSchedule = remember {
        listOf(
            ScheduleItem("7:00", "Подъём", "🌅", 7, 0),
            ScheduleItem("7:30", "Зарядка", "🏃", 7, 30),
            ScheduleItem("8:00", "Завтрак", "🍳", 8, 0),
            ScheduleItem("9:00", "Учёба", "📚", 9, 0),
            ScheduleItem("10:00", "Прогулка с Бимом", "🐶", 10, 0),
            ScheduleItem("12:00", "Обед", "🍲", 12, 0),
            ScheduleItem("13:00", "Игры", "🎮", 13, 0),
            ScheduleItem("15:00", "Полдник", "🍪", 15, 0),
            ScheduleItem("16:00", "Помощь по дому", "🏠", 16, 0),
            ScheduleItem("18:00", "Ужин", "🍝", 18, 0),
            ScheduleItem("19:00", "Чтение", "📖", 19, 0),
            ScheduleItem("20:00", "Подготовка ко сну", "🛁", 20, 0),
            ScheduleItem("21:00", "Сон", "😴", 21, 0)
        )
    }
    
    var currentScheduleIndex by remember { mutableIntStateOf(2) }
    var score by remember { mutableIntStateOf(0) }
    var showInstructions by remember { mutableStateOf(true) }
    var gameMode by remember { mutableStateOf(GameMode.LEARN) }
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    
    var targetActivity by remember { mutableStateOf(fullSchedule.random()) }
    var showFeedback by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    
    val feedbackScale by animateFloatAsState(
        targetValue = if (showFeedback.isNotEmpty()) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val currentItem = fullSchedule[currentScheduleIndex]
    
    LaunchedEffect(showFeedback) {
        if (showFeedback.isNotEmpty()) {
            delay(2000)
            showFeedback = ""
            if (isCorrect == true) {
                targetActivity = fullSchedule.random()
                selectedHour = 12
                selectedMinute = 0
            }
            isCorrect = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2),
                        Color(0xFFFFCC80)
                    )
                )
            )
    ) {
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                title = {
                    Text("🕐 Часы Кузи", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                },
                text = {
                    Text(
                        "Помоги Кузе научиться определять время!\n\n" +
                        "🎓 Режим «Учёба»:\n" +
                        "• Смотри на расписание Кузи\n" +
                        "• Нажимай на время, чтобы увидеть активность\n\n" +
                        "🎮 Режим «Игра»:\n" +
                        "• Установи правильное время для активности\n" +
                        "• Перетаскивай стрелки часов\n\n" +
                        "🌈 Изучай режим дня вместе с Кузей!",
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Понятно! 🚀", fontSize = 18.sp)
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
                tint = Color(0xFFE65100)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🕐 Режим дня Кузи",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = gameMode == GameMode.LEARN,
                            onClick = { gameMode = GameMode.LEARN },
                            label = { Text("🎓 Учёба") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE0B2)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = gameMode == GameMode.PLAY,
                            onClick = { 
                                gameMode = GameMode.PLAY
                                targetActivity = fullSchedule.random()
                            },
                            label = { Text("🎮 Игра") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE0B2)
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (gameMode) {
                GameMode.LEARN -> {
                    LearnMode(
                        schedule = fullSchedule,
                        currentIndex = currentScheduleIndex,
                        onTimeClick = { index ->
                            currentScheduleIndex = index
                            selectedHour = fullSchedule[index].hour
                            selectedMinute = fullSchedule[index].minute
                        }
                    )
                }
                
                GameMode.PLAY -> {
                    PlayMode(
                        targetActivity = targetActivity,
                        selectedHour = selectedHour,
                        selectedMinute = selectedMinute,
                        onHourChange = { selectedHour = it },
                        onMinuteChange = { selectedMinute = it },
                        onCheck = {
                            val isCorrectTime = selectedHour == targetActivity.hour && 
                                               abs(selectedMinute - targetActivity.minute) <= 15
                            
                            if (isCorrectTime) {
                                isCorrect = true
                                score += 10
                                showFeedback = "✅ Правильно! ${targetActivity.activity} в ${targetActivity.time}"
                            } else {
                                isCorrect = false
                                showFeedback = "❌ Не совсем! Попробуй ещё раз"
                            }
                        },
                        showFeedback = showFeedback,
                        isCorrect = isCorrect,
                        feedbackScale = feedbackScale
                    )
                }
            }
            
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
                    Text(text = "Сейчас:", fontSize = 18.sp, color = Color.Gray)
                    Text(text = currentItem.emoji, fontSize = 48.sp)
                    Text(
                        text = "${currentItem.time} - ${currentItem.activity}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (gameMode == GameMode.PLAY) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Очки: $score",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LearnMode(
    schedule: List<ScheduleItem>,
    currentIndex: Int,
    onTimeClick: (Int) -> Unit
) {
    val currentItem = schedule[currentIndex]
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ClockFace(
            hour = currentItem.hour,
            minute = currentItem.minute,
            size = 250.dp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Выбери время:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(schedule) { item ->
                val index = schedule.indexOf(item)
                TimeButton(
                    time = item.time,
                    emoji = item.emoji,
                    activity = item.activity,
                    isSelected = index == currentIndex,
                    onClick = { onTimeClick(index) }
                )
            }
        }
    }
}

@Composable
fun PlayMode(
    targetActivity: ScheduleItem,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onCheck: () -> Unit,
    showFeedback: String,
    isCorrect: Boolean?,
    feedbackScale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Установи время для:", fontSize = 18.sp, color = Color.Gray)
                Text(text = targetActivity.emoji, fontSize = 48.sp)
                Text(
                    text = targetActivity.activity,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InteractiveClockFace(
            hour = selectedHour,
            minute = selectedMinute,
            onHourChange = onHourChange,
            onMinuteChange = onMinuteChange,
            size = 220.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = String.format("%02d:%02d", selectedHour, selectedMinute),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onCheck,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp)
                .scale(feedbackScale),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(text = "✅ Проверить", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        AnimatedVisibility(
            visible = showFeedback.isNotEmpty(),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isCorrect == true -> Color(0xFFC8E6C9)
                        isCorrect == false -> Color(0xFFFFCDD2)
                        else -> Color.White
                    }
                )
            ) {
                Text(
                    text = showFeedback,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCorrect == true -> Color(0xFF2E7D32)
                        isCorrect == false -> Color(0xFFC62828)
                        else -> Color.Black
                    },
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ClockFace(hour: Int, minute: Int, size: Dp) {
    val colorPrimary = Color(0xFFE65100)
    val colorSecondary = Color(0xFFBF360C)
    val colorBackground = Color.White
    
    Canvas(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(colorBackground)
            .border(4.dp, colorPrimary, CircleShape)
    ) {
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val radius = size.toPx() / 2 - 20.dp.toPx()
        
        for (i in 1..12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val textRadius = radius * 0.75f
            val x = center.x + (textRadius * cos(angle)).toFloat()
            val y = center.y + (textRadius * sin(angle)).toFloat()
            
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    i.toString(), x, y + 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        textSize = 24.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.parseColor("#E65100")
                        isFakeBoldText = true
                    }
                )
            }
        }
        
        for (i in 0..59) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val startRadius = if (i % 5 == 0) radius * 0.85f else radius * 0.92f
            val endRadius = radius * 0.98f
            
            drawLine(
                color = if (i % 5 == 0) colorSecondary else Color.Gray.copy(alpha = 0.5f),
                start = Offset(
                    center.x + (startRadius * cos(angle)).toFloat(),
                    center.y + (startRadius * sin(angle)).toFloat()
                ),
                end = Offset(
                    center.x + (endRadius * cos(angle)).toFloat(),
                    center.y + (endRadius * sin(angle)).toFloat()
                ),
                strokeWidth = if (i % 5 == 0) 3.dp.toPx() else 1.dp.toPx()
            )
        }
        
        val hourAngle = Math.toRadians(((hour % 12) * 30 + minute * 0.5 - 90).toDouble())
        val hourLength = radius * 0.5f
        
        drawLine(
            color = colorPrimary, start = center,
            end = Offset(
                center.x + (hourLength * cos(hourAngle)).toFloat(),
                center.y + (hourLength * sin(hourAngle)).toFloat()
            ),
            strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round
        )
        
        val minuteAngle = Math.toRadians((minute * 6 - 90).toDouble())
        val minuteLength = radius * 0.7f
        
        drawLine(
            color = colorSecondary, start = center,
            end = Offset(
                center.x + (minuteLength * cos(minuteAngle)).toFloat(),
                center.y + (minuteLength * sin(minuteAngle)).toFloat()
            ),
            strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round
        )
        
        drawCircle(color = colorPrimary, radius = 8.dp.toPx(), center = center)
        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = center)
    }
}

@Composable
fun InteractiveClockFace(
    hour: Int, minute: Int,
    onHourChange: (Int) -> Unit, onMinuteChange: (Int) -> Unit,
    size: Dp
) {
    val colorPrimary = Color(0xFFE65100)
    val colorSecondary = Color(0xFFBF360C)
    
    var isDraggingHour by remember { mutableStateOf(false) }
    var isDraggingMinute by remember { mutableStateOf(false) }
    
    Canvas(
        modifier = Modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.toPx() / 2, size.toPx() / 2)
                        if ((offset - center).getDistance() < size.toPx() * 0.4f) {
                            isDraggingHour = true
                        } else {
                            isDraggingMinute = true
                        }
                    },
                    onDrag = { change, _ ->
                        val center = Offset(size.toPx() / 2, size.toPx() / 2)
                        val angle = atan2(
                            (change.position.y - center.y).toDouble(),
                            (change.position.x - center.x).toDouble()
                        )
                        val degrees = Math.toDegrees(angle) + 90
                        val normalizedDegrees = if (degrees < 0) degrees + 360 else degrees
                        
                        if (isDraggingHour) {
                            val newHour = ((normalizedDegrees / 30).toInt() % 12)
                            onHourChange(if (newHour == 0) 12 else newHour)
                        } else if (isDraggingMinute) {
                            onMinuteChange(((normalizedDegrees / 6).toInt() % 60))
                        }
                    },
                    onDragEnd = {
                        isDraggingHour = false
                        isDraggingMinute = false
                    }
                )
            }
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .border(4.dp, colorPrimary, CircleShape)
    ) {
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val radius = size.toPx() / 2 - 20.dp.toPx()
        
        for (i in 1..12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val textRadius = radius * 0.75f
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    i.toString(),
                    center.x + (textRadius * cos(angle)).toFloat(),
                    center.y + (textRadius * sin(angle)).toFloat() + 8.dp.toPx(),
                    android.graphics.Paint().apply {
                        textSize = 24.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        color = android.graphics.Color.parseColor("#E65100")
                        isFakeBoldText = true
                    }
                )
            }
        }
        
        for (i in 0..59) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val startR = if (i % 5 == 0) radius * 0.85f else radius * 0.92f
            val endR = radius * 0.98f
            drawLine(
                color = if (i % 5 == 0) colorSecondary else Color.Gray.copy(alpha = 0.5f),
                start = Offset(center.x + (startR * cos(angle)).toFloat(), center.y + (startR * sin(angle)).toFloat()),
                end = Offset(center.x + (endR * cos(angle)).toFloat(), center.y + (endR * sin(angle)).toFloat()),
                strokeWidth = if (i % 5 == 0) 3.dp.toPx() else 1.dp.toPx()
            )
        }
        
        val hourAngle = Math.toRadians(((hour % 12) * 30 + minute * 0.5 - 90).toDouble())
        drawLine(
            color = if (isDraggingHour) Color(0xFFFFA000) else colorPrimary,
            start = center,
            end = Offset(center.x + (radius * 0.5f * cos(hourAngle)).toFloat(), center.y + (radius * 0.5f * sin(hourAngle)).toFloat()),
            strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round
        )
        
        val minuteAngle = Math.toRadians((minute * 6 - 90).toDouble())
        drawLine(
            color = if (isDraggingMinute) Color(0xFFFFA000) else colorSecondary,
            start = center,
            end = Offset(center.x + (radius * 0.7f * cos(minuteAngle)).toFloat(), center.y + (radius * 0.7f * sin(minuteAngle)).toFloat()),
            strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round
        )
        
        drawCircle(color = colorPrimary, radius = 8.dp.toPx(), center = center)
        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = center)
    }
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "🖐 Перетаскивай стрелки", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun TimeButton(time: String, emoji: String, activity: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
            .scale(if (isSelected) 1.1f else 1f)
            .then(if (isSelected) Modifier.border(3.dp, Color(0xFFE65100), RoundedCornerShape(12.dp)) else Modifier),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFE0B2) else Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 32.sp)
            Text(text = time, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            Text(text = activity, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

enum class GameMode { LEARN, PLAY }
