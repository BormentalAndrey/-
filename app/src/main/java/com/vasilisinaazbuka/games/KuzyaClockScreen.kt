package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun KuzyaClockScreen(onBackClick: () -> Unit) {
    val schedule = mapOf("7:00" to "Подъём", "8:00" to "Завтрак", "10:00" to "Прогулка с Бимом")
    var targetTime by remember { mutableStateOf("8:00") }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(schedule[targetTime] ?: "", fontSize = 36.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Визуализация часов (Заглушка для Custom Canvas)
            Box(
                modifier = Modifier.size(250.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🕐", fontSize = 150.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                schedule.keys.forEach { time ->
                    Button(onClick = { if (time == targetTime) targetTime = "10:00" }, modifier = Modifier.size(150.dp, 80.dp)) {
                        Text(time, fontSize = 32.sp)
                    }
                }
            }
        }
    }
}
