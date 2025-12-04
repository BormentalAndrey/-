package com.kakdela.p2p.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakdela.p2p.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel, chatId: Int, navController: NavController) {
    LaunchedEffect(chatId) { viewModel.loadMessages(chatId) }
    val messages = viewModel.messages.collectAsState().value
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text(message.text ?: "")
            }
        }

        TextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            if (text.isNotBlank()) {
                viewModel.sendMessage(chatId, text, true)
                text = ""
            }
        }) { Text("Отправить") }
    }
}
