package com.kakdela.p2p.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: Int,
    val text: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByMe: Boolean = true
)
