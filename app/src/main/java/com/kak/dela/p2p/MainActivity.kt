package com.kakdela.p2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kakdela.p2p.ui.ChatListScreen
import com.kakdela.p2p.ui.ChatScreen
import com.kakdela.p2p.viewmodel.ChatViewModel
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "chatList") {
                        composable("chatList") {
                            ChatListScreen(viewModel, navController)
                        }
                        composable("chat/{chatId}") { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId")?.toIntOrNull() ?: 0
                            ChatScreen(viewModel, chatId, navController)
                        }
                    }
                }
            }
        }
    }
}
