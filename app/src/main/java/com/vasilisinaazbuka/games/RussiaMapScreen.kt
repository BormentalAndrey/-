package com.vasilisinaazbuka.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class RegionPuzzle(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val position: Offset,
    val color: Color,
    val facts: List<String>
)

@Composable
fun RussiaMapScreen(onBackClick: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var placedPuzzles by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    var showInstructions by remember { mutableStateOf(true) }
    var showFacts by remember { mutableStateOf(false) }
    var currentFact by remember { mutableStateOf("") }
    var showCelebration by remember { mutableStateOf(false) }
    
    var selectedPuzzle by remember { mutableStateOf<RegionPuzzle?>(null) }
    var draggedPuzzle by remember { mutableStateOf<RegionPuzzle?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var showFeedback by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    
    var availablePuzzles by remember { mutableStateOf(generatePuzzles(level)) }
    var placedPuzzlesList by remember { mutableStateOf(listOf<RegionPuzzle>()) }
    
    val feedbackScale by animateFloatAsState(
        targetValue = if (showFeedback.isNotEmpty()) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val celebrationScale by animateFloatAsState(
        targetValue = if (showCelebration) 1.2f else 1f,
        animationSpec = repeatable(
            iterations = if (showCelebration) 5 else 1,
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(showFeedback) {
        if (showFeedback.isNotEmpty()) {
            delay(2000)
            if (availablePuzzles.isNotEmpty() || !showCelebration) {
                showFeedback = ""
                isCorrect = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB),
                        Color(0xFF90CAF9)
                    )
                )
            )
    ) {
        if (showInstructions) {
            AlertDialog(
                onDismissRequest = { showInstructions = false },
                title = {
                    Text("🗺️ Карта России", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                },
                text = {
                    Text(
                        "Собери карту России из пазлов!\n\n" +
                        "🎯 Как играть:\n" +
                        "1. Выбери пазл слева\n" +
                        "2. Перетащи его на правильное место\n" +
                        "3. Узнавай интересные факты о регионах\n\n" +
                        "🌟 Чем точнее разместишь, тем больше очков!\n" +
                        "📚 С каждым уровнем узнаёшь новые места",
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = false }) {
                        Text("Начать собирать! 🚀", fontSize = 18.sp)
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
        
        if (showFacts) {
            AlertDialog(
                onDismissRequest = { showFacts = false },
                title = {
                    Text("📚 Интересный факт!", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(currentFact, fontSize = 16.sp, lineHeight = 24.sp)
                },
                confirmButton = {
                    TextButton(onClick = { showFacts = false }) {
                        Text("Здорово! 👍")
                    }
                },
                shape = RoundedCornerShape(16.dp)
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

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBadge("⭐", "$score", Color(0xFFFFA000))
                            StatBadge("🎯", "Ур.$level", Color(0xFF1976D2))
                            StatBadge("🗺️", "$placedPuzzles/${placedPuzzles + availablePuzzles.size}", Color(0xFF4CAF50))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "🧩 Детали пазла:",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (availablePuzzles.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availablePuzzles.size) { index ->
                            val puzzle = availablePuzzles[index]
                            PuzzleCard(
                                puzzle = puzzle,
                                isSelected = selectedPuzzle == puzzle,
                                onClick = {
                                    selectedPuzzle = if (selectedPuzzle == puzzle) null else puzzle
                                    draggedPuzzle = null
                                },
                                onDragStart = { offset ->
                                    draggedPuzzle = puzzle
                                    dragOffset = offset
                                }
                            )
                        }
                    }
                } else if (showCelebration) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(celebrationScale),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🎉", fontSize = 48.sp)
                            Text(
                                "Уровень $level\nпройден!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.Center
                            )
                            Text("Счёт: $score", fontSize = 18.sp, color = Color(0xFF1976D2))
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        level = 1
                                        score = 0
                                        mistakes = 0
                                        showCelebration = false
                                        availablePuzzles = generatePuzzles(1)
                                        placedPuzzlesList = emptyList()
                                        placedPuzzles = 0
                                    }
                                ) {
                                    Text("🔄")
                                }
                                
                                Button(
                                    onClick = {
                                        level++
                                        score += 50
                                        showCelebration = false
                                        availablePuzzles = generatePuzzles(level)
                                        placedPuzzlesList = emptyList()
                                        placedPuzzles = 0
                                    }
                                ) {
                                    Text("▶️ Далее")
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF1565C0), RoundedCornerShape(16.dp))
            ) {
                RussiaMap(
                    placedPuzzles = placedPuzzlesList,
                    selectedPuzzle = selectedPuzzle,
                    draggedPuzzle = draggedPuzzle,
                    dragOffset = dragOffset,
                    onDrop = { puzzle, position ->
                        val targetPos = puzzle.position
                        val distance = (position - targetPos).getDistance()
                        
                        if (distance < 0.15f) {
                            isCorrect = true
                            score += 20 * level
                            placedPuzzles++
                            
                            placedPuzzlesList = placedPuzzlesList + puzzle
                            availablePuzzles = availablePuzzles.filter { it.id != puzzle.id }
                            
                            currentFact = puzzle.facts.random()
                            showFacts = true
                            
                            showFeedback = "✅ Правильно! ${puzzle.name} на месте!"
                            
                            if (availablePuzzles.isEmpty()) {
                                showCelebration = true
                                showFeedback = "🌟 Карта собрана! Уровень $level пройден!"
                            }
                        } else {
                            isCorrect = false
                            mistakes++
                            score = (score - 5).coerceAtLeast(0)
                            showFeedback = "❌ Не совсем! Попробуй разместить ${puzzle.name} в другом месте"
                        }
                        
                        selectedPuzzle = null
                        draggedPuzzle = null
                    }
                )
                
                if (showFeedback.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .scale(feedbackScale)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isCorrect == true -> Color(0xFFC8E6C9)
                                    isCorrect == false -> Color(0xFFFFCDD2)
                                    else -> Color.White
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = showFeedback,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isCorrect == true -> Color(0xFF2E7D32)
                                    isCorrect == false -> Color(0xFFC62828)
                                    else -> Color.Black
                                },
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                if (availablePuzzles.isNotEmpty()) {
                    Text(
                        text = "🗺️ Перетащи пазл на карту",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RussiaMap(
    placedPuzzles: List<RegionPuzzle>,
    selectedPuzzle: RegionPuzzle?,
    draggedPuzzle: RegionPuzzle?,
    dragOffset: Offset,
    onDrop: (RegionPuzzle, Offset) -> Unit
) {
    var mapSize by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onSizeChanged { size ->
                mapSize = Offset(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, _ -> },
                    onDragEnd = {
                        draggedPuzzle?.let { puzzle ->
                            if (mapSize.x > 0 && mapSize.y > 0) {
                                val normalizedX = dragOffset.x / mapSize.x
                                val normalizedY = dragOffset.y / mapSize.y
                                onDrop(puzzle, Offset(normalizedX, normalizedY))
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            val gridColor = Color(0xFFE0E0E0)
            for (i in 0..10) {
                val x = width * i / 10
                val y = height * i / 10
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
                drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
            }
            
            val russiaPath = Path().apply {
                moveTo(width * 0.15f, height * 0.3f)
                lineTo(width * 0.25f, height * 0.2f)
                lineTo(width * 0.4f, height * 0.15f)
                lineTo(width * 0.6f, height * 0.2f)
                lineTo(width * 0.8f, height * 0.25f)
                lineTo(width * 0.9f, height * 0.4f)
                lineTo(width * 0.85f, height * 0.6f)
                lineTo(width * 0.7f, height * 0.7f)
                lineTo(width * 0.5f, height * 0.75f)
                lineTo(width * 0.3f, height * 0.7f)
                lineTo(width * 0.1f, height * 0.5f)
                close()
            }
            
            drawPath(path = russiaPath, color = Color(0xFFE3F2FD), style = Fill)
            drawPath(path = russiaPath, color = Color(0xFF1565C0), style = Stroke(width = 3f))
            
            drawContext.canvas.nativeCanvas.drawText(
                "РОССИЯ",
                width * 0.35f,
                height * 0.5f,
                android.graphics.Paint().apply {
                    textSize = 48.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = android.graphics.Color.parseColor("#90CAF9")
                    isFakeBoldText = true
                }
            )
        }
        
        generatePuzzles(1).forEach { puzzle ->
            val targetX = puzzle.position.x * mapSize.x
            val targetY = puzzle.position.y * mapSize.y
            
            if (!placedPuzzles.any { it.id == puzzle.id }) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (targetX - 25.dp.toPx()).roundToInt(),
                                (targetY - 25.dp.toPx()).roundToInt()
                            )
                        }
                        .size(50.dp)
                        .border(
                            2.dp,
                            if (selectedPuzzle?.id == puzzle.id) Color(0xFF4CAF50) else Color(0xFFBBDEFB),
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            if (selectedPuzzle?.id == puzzle.id) Color(0xFFC8E6C9).copy(alpha = 0.5f)
                            else Color(0xFFF5F5F5).copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📍", fontSize = 20.sp)
                }
            }
        }
        
        placedPuzzles.forEach { puzzle ->
            val posX = puzzle.position.x * mapSize.x
            val posY = puzzle.position.y * mapSize.y
            
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (posX - 25.dp.toPx()).roundToInt(),
                            (posY - 25.dp.toPx()).roundToInt()
                        )
                    }
                    .size(50.dp)
                    .background(puzzle.color.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(puzzle.emoji, fontSize = 24.sp)
                    Text(puzzle.name, fontSize = 8.sp, textAlign = TextAlign.Center, color = Color.White)
                }
            }
        }
        
        if (draggedPuzzle != null && mapSize.x > 0 && mapSize.y > 0) {
            val dragX = dragOffset.x
            val dragY = dragOffset.y
            
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragX - 25.dp.toPx()).roundToInt(),
                            (dragY - 25.dp.toPx()).roundToInt()
                        )
                    }
                    .size(50.dp)
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .background(draggedPuzzle.color, RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFFFFA000), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(draggedPuzzle.emoji, fontSize = 24.sp)
                    Text(draggedPuzzle.name, fontSize = 8.sp, textAlign = TextAlign.Center, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PuzzleCard(
    puzzle: RegionPuzzle,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDragStart: (Offset) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onDragStart(offset); onClick() },
                    onDrag = { _, _ -> }
                )
            }
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, Color(0xFFFFA000), RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) puzzle.color.copy(alpha = 0.2f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = puzzle.emoji, fontSize = 32.sp)
                Column {
                    Text(text = puzzle.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF424242))
                    Text(text = puzzle.description, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Icon(Icons.Default.DragHandle, contentDescription = "Перетащить", tint = Color.Gray)
        }
    }
}

@Composable
fun StatBadge(emoji: String, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

fun generatePuzzles(level: Int): List<RegionPuzzle> {
    val allPuzzles = listOf(
        RegionPuzzle("moscow", "Москва", "🏛️", "Столица России", Offset(0.15f, 0.4f), Color(0xFFD32F2F),
            listOf("Москва — самый большой город России!", "В Москве находится Красная площадь и Кремль", "Московское метро — одно из самых красивых в мире")),
        RegionPuzzle("baikal", "Байкал", "🌊", "Самое глубокое озеро", Offset(0.5f, 0.65f), Color(0xFF1976D2),
            listOf("Байкал — самое глубокое озеро в мире!", "В Байкале содержится 20% всей пресной воды планеты", "Озеру более 25 миллионов лет")),
        RegionPuzzle("kamchatka", "Камчатка", "🌋", "Край вулканов", Offset(0.85f, 0.45f), Color(0xFFF57C00),
            listOf("На Камчатке более 300 вулканов!", "Там живут медведи, лисы и северные олени", "Долина гейзеров — чудо природы")),
        RegionPuzzle("sochi", "Сочи", "🌴", "Южная столица", Offset(0.05f, 0.65f), Color(0xFF388E3C),
            listOf("Сочи — самый тёплый город России", "Здесь проходила Олимпиада 2014 года", "В Сочи растут пальмы и чай")),
        RegionPuzzle("ural", "Урал", "⛰️", "Граница Европы и Азии", Offset(0.3f, 0.5f), Color(0xFF7B1FA2),
            listOf("Уральские горы разделяют Европу и Азию", "Здесь добывают малахит и самоцветы", "Урал — сокровищница полезных ископаемых")),
        RegionPuzzle("siberia", "Сибирь", "❄️", "Великая земля", Offset(0.6f, 0.4f), Color(0xFF0097A7),
            listOf("Сибирь занимает 77% территории России", "Здесь находится полюс холода — Оймякон", "В Сибири живут соболя и сибирские тигры")),
        RegionPuzzle("petersburg", "Санкт-Петербург", "🌉", "Северная столица", Offset(0.1f, 0.25f), Color(0xFFC2185B),
            listOf("Петербург называют Северной Венецией", "Здесь 342 моста через реки и каналы", "Эрмитаж — один из крупнейших музеев мира")),
        RegionPuzzle("altai", "Алтай", "🏔️", "Золотые горы", Offset(0.45f, 0.7f), Color(0xFFFFA000),
            listOf("Алтай называют «Золотыми горами»", "Здесь чистейший воздух и горный мёд", "Телецкое озеро — жемчужина Алтая"))
    )
    
    val puzzleCount = when (level) { 1 -> 3; 2 -> 4; 3 -> 5; 4 -> 6; 5 -> 7; else -> 8 }
    return allPuzzles.take(puzzleCount).shuffled()
}
