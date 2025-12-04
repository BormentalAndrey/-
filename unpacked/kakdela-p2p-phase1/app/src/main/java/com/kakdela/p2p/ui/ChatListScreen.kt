package com.kakdela.p2p.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakdela.p2p.viewmodel.ChatViewModel

@Composable
fun ChatListScreen(viewModel: ChatViewModel, navController: NavController) {
    val chats = viewModel.chats.collectAsState().value

    LazyColumn {
        items(chats) { chat ->
            ListItem(
                headlineContent = { Text(chat.name) },
                supportingContent = { Text(chat.lastMessage) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("chat/${chat.id}") }
                    .padding(vertical = 8.dp)
            )
            Divider()
        }
    }
}
