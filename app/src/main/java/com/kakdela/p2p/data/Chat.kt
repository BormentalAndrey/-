package com.kakdela.p2p.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lastMessage: String = "",
    val lastTimestamp: Long = 0
)
