package com.kakdela.p2p.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class ChatRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "kakdela_db"
    ).fallbackToDestructiveMigration().build()
    private val dao = db.chatDao()

    fun getAllChats(): Flow<List<Chat>> = dao.getAllChats()

    suspend fun insertChat(chat: Chat) = dao.insertChat(chat)

    fun getMessagesForChat(chatId: Int): Flow<List<Message>> = dao.getMessagesForChat(chatId)

    suspend fun insertMessage(message: Message) = dao.insertMessage(message)

    suspend fun updateChatLastMessage(chatId: Int, lastMessage: String, lastTimestamp: Long) = dao.updateChatLastMessage(chatId, lastMessage, lastTimestamp)
}
