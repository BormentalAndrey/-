package com.vasilisinaazbuka.games

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
fun ForestSortScreen(onBackClick: () -> Unit) {
    var selectedItem by remember { mutableStateOf<String?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopEnd).zIndex(100f).padding(16.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Назад", modifier = Modifier.size(48.dp))
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Собери находки в лесу!", fontSize = 32.sp)
            
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Text("🍄", fontSize = 60.sp, modifier = Modifier.clickable { selectedItem = "🍄" })
                Text("🫐", fontSize = 60.sp, modifier = Modifier.clickable { selectedItem = "🫐" })
                Text("🐿️", fontSize = 60.sp, modifier = Modifier.clickable { selectedItem = "🐿️" })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Basket("Корзина для грибов") { if (selectedItem == "🍄") selectedItem = null }
                Basket("Корзина для ягод") { if (selectedItem == "🫐") selectedItem = null }
                Basket("Для животных") { if (selectedItem == "🐿️") selectedItem = null }
            }
        }
    }
}

@Composable
fun Basket(name: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(160.dp, 100.dp)) {
        Text(name, fontSize = 18.sp)
    }
}
