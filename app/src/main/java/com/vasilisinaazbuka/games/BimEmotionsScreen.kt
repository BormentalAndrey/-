package com.vasilisinaazbuka.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

data class Emotion(
    val emoji: String,
    val name: String,
    val imageResId: Int
)

@Composable
fun BimEmotionsScreen(onBackClick: () -> Unit) {
    val emotionsList = listOf(
        Emotion("🐶😊", "Радость", R.drawable.bim_happy),
        Emotion("🐶😢", "Грусть", R.drawable.bim_sad),
        Emotion("🐶😠", "Злость", R.drawable.bim_angry),
        Emotion("🐶😮", "Удивление", R.drawable.bim_surprised),
        Emotion("🐶😨", "Страх", R.drawable.bim_scared),
        Emotion("😴", "Сон", R.drawable.bim_sleepy),
        Emotion("🍖", "Голод", R.drawable.bim_hungry),
        Emotion("🎾", "Игра", R.drawable.bim_play),
        Emotion("❤️", "Любовь", R.drawable.bim_love),
        Emotion("🤒", "Болезнь", R.drawable.bim_sick)
    )
    
    var currentEmotion by remember { mutableStateOf(emotionsList.random()) }
    var isCorrectAnswer by remember { mutableStateOf<Boolean?>(null) }
    var options by remember { mutableStateOf(generateOptions(emotionsList, currentEmotion)) }
    var showNextButton by remember { mutableStateOf(false) }
    
    val feedbackScale by animateFloatAsState(
        targetValue = if (isCorrectAnswer != null) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Что чувствует Бим?",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = currentEmotion.imageResId),
                    contentDescription = currentEmotion.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isCorrectAnswer != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .scale(feedbackScale),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrectAnswer == true) 
                            Color(0xFFC8E6C9) 
                        else 
                            Color(0xFFFFCDD2)
                    )
                ) {
                    Text(
                        text = if (isCorrectAnswer == true) "✅ Правильно! Это ${currentEmotion.name}"
                               else "❌ Попробуй ещё раз!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrectAnswer == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            if (option == currentEmotion) {
                                isCorrectAnswer = true
                                showNextButton = true
                            } else {
                                isCorrectAnswer = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isCorrectAnswer == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100)
                        )
                    ) {
                        Text(
                            option.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
            
            if (showNextButton) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        currentEmotion = emotionsList.random()
                        options = generateOptions(emotionsList, currentEmotion)
                        isCorrectAnswer = null
                        showNextButton = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Далее ▶️", fontSize = 20.sp)
                }
            }
        }
    }
}

fun generateOptions(emotions: List<Emotion>, correct: Emotion): List<Emotion> {
    val others = emotions.filter { it != correct }.shuffled()
    return (listOf(correct) + others.take(2)).shuffled()
}
