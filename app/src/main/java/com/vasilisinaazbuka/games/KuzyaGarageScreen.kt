package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun KuzyaGarageScreen(onBackClick: () -> Unit) {
    // Упрощенная механика: выбираем машинку -> кликаем на гараж
    var selectedCar by remember { mutableStateOf<String?>(null) }
    val cars = listOf("🚗 (Красная)", "🚙 (Синяя)", "🚕 (Желтая)")

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Помоги Кузе расставить машинки по цветам!", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(32.dp))

            // Гаражи
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                GarageBox("Красный гараж", Color.Red) { if (selectedCar?.contains("Красная") == true) selectedCar = null }
                GarageBox("Синий гараж", Color.Blue) { if (selectedCar?.contains("Синяя") == true) selectedCar = null }
                GarageBox("Желтый гараж", Color.Yellow) { if (selectedCar?.contains("Желтая") == true) selectedCar = null }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Машинки для выбора
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                cars.forEach { car ->
                    Text(
                        text = car,
                        fontSize = 50.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { selectedCar = car }
                            .background(if (selectedCar == car) Color.LightGray else Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun GarageBox(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(150.dp, 120.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(title, fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}
