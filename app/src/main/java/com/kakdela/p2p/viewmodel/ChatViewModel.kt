package com.kakdela.p2p.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kakdela.p2p.data.Chat
import com.kakdela.p2p.data.ChatRepository
import com.kakdela.p2p.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        viewModelScope.launch {
            repository.getAllChats().collect { list ->
                _chats.value = list
            }
        }
    }

    fun loadMessages(chatId: Int) {
        viewModelScope.launch {
            repository.getMessagesForChat(chatId).collect { list ->
                _messages.value = list
            }
        }
    }

    fun sendMessage(chatId: Int, text: String, isSentByMe: Boolean) {
        viewModelScope.launch {
            val message = Message(chatId = chatId, text = text, isSentByMe = isSentByMe)
            repository.insertMessage(message)
            repository.updateChatLastMessage(chatId, text, message.timestamp)
            loadMessages(chatId)
        }
    }

    fun createChat(name: String) {
        viewModelScope.launch {
            val chat = Chat(name = name, lastTimestamp = System.currentTimeMillis())
            repository.insertChat(chat)
        }
    }
}
