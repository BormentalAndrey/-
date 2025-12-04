package com.kakdela.p2p.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastTimestamp DESC")
    fun getAllChats(): Flow<List<Chat>>

    @Insert
    suspend fun insertChat(chat: Chat)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: Int): Flow<List<Message>>

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("UPDATE chats SET lastMessage = :lastMessage, lastTimestamp = :lastTimestamp WHERE id = :chatId")
    suspend fun updateChatLastMessage(chatId: Int, lastMessage: String, lastTimestamp: Long)
}
