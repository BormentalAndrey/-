package com.vasilisinaazbuka.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.random.Random

@Composable
fun HappyCountingScreen(onBackClick: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    var targetCount by remember { mutableIntStateOf(Random.nextInt(1, 4)) }
    var options by remember { mutableStateOf(generateOptions(targetCount)) }
    var bimEmotion by remember { mutableStateOf("🐶🤔") }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Персонажи слева
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("👦 Кузя", fontSize = 60.sp)
                Text(bimEmotion, fontSize = 60.sp)
            }

            // Игровая зона
            Column(
                modifier = Modifier.weight(2f).fillMaxHeight().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Предметы для счета
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(targetCount) { Text("🎾", fontSize = 50.sp) }
                }

                // Кнопки с вариантами
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    options.forEach { option ->
                        Button(
                            onClick = {
                                if (option == targetCount) {
                                    bimEmotion = "🐶🎉"
                                    // Переход на следующий раунд
                                    targetCount = Random.nextInt(1, 6) // Для уровня 2
                                    options = generateOptions(targetCount)
                                } else {
                                    bimEmotion = "🐶😢"
                                }
                            },
                            modifier = Modifier.size(100.dp)
                        ) {
                            Text(option.toString(), fontSize = 40.sp)
                        }
                    }
                }
            }
        }
    }
}

fun generateOptions(correct: Int): List<Int> {
    val options = mutableSetOf(correct)
    while (options.size < 3) {
        val wrong = correct + Random.nextInt(-2, 3)
        if (wrong > 0 && wrong != correct) options.add(wrong)
    }
    return options.shuffled()
}
