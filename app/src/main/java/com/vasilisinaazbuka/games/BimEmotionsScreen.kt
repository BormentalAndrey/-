package com.vasilisinaazbuka.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.random.Random

@Composable
fun BimEmotionsScreen(onBackClick: () -> Unit) {
    data class Emotion(
        val emoji: String,
        val name: String,
        val imageResId: Int
    )
    
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
    
    fun generateOptions(allEmotions: List<Emotion>, correctEmotion: Emotion): List<Emotion> {
        val otherEmotions = allEmotions.filter { it != correctEmotion }
        val randomOptions = otherEmotions.shuffled().take(2)
        return (randomOptions + correctEmotion).shuffled()
    }
    
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Кнопка закрытия
        IconButton(
            onClick = onBackClick, 
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(100f)
                .size(48.dp)
        ) {
            Icon(
                Icons.Default.Close, 
                contentDescription = "Закрыть", 
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Заголовок
            Text(
                text = "Что чувствует Бим?",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Изображение Бима с эмоцией
            Card(
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = currentEmotion.imageResId),
                    contentDescription = currentEmotion.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            
            // Индикатор правильного/неправильного ответа
            if (isCorrectAnswer != null) {
                Text(
                    text = if (isCorrectAnswer == true) "✅ Правильно!" else "❌ Попробуй ещё раз",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrectAnswer == true) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Кнопки с вариантами ответов
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            isCorrectAnswer = option == currentEmotion
                            if (isCorrectAnswer == true) {
                                // Через небольшую задержку показываем новую эмоцию
                                currentEmotion = emotionsList.random()
                                options = generateOptions(emotionsList, currentEmotion)
                                isCorrectAnswer = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(70.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrectAnswer != null && option == currentEmotion) 
                                MaterialTheme.colorScheme.primaryContainer
                            else 
                                MaterialTheme.colorScheme.primary
                        ),
                        enabled = !(isCorrectAnswer == true && option != currentEmotion)
                    ) {
                        Text(
                            text = option.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Счетчик или дополнительная информация
            Text(
                text = "Выбери правильную эмоцию",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
