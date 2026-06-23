package com.vasilisinaazbuka.games

import androidx.compose.foundation.background
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
fun RussiaMapScreen(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE3F2FD))) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Зона пазлов
            Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                Text("Детали пазла:", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Выбор региона */ }, modifier = Modifier.fillMaxWidth().height(60.dp)) { Text("Кремль (Москва)") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Выбор региона */ }, modifier = Modifier.fillMaxWidth().height(60.dp)) { Text("Байкал") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Выбор региона */ }, modifier = Modifier.fillMaxWidth().height(60.dp)) { Text("Камчатка") }
            }

            // Карта (целевая зона)
            Box(
                modifier = Modifier.weight(2f).fillMaxHeight().padding(16.dp).background(Color(0xFFA5D6A7)),
                contentAlignment = Alignment.Center
            ) {
                Text("🗺️ Карта России (Фон)", fontSize = 36.sp, color = Color.DarkGray)
                // Здесь будет Canvas с координатами Drop-зон
            }
        }
    }
}
