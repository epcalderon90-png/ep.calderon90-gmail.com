package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String, // format: "itemId_buyerId_donorId"
    val itemId: Int,
    val itemTitle: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
