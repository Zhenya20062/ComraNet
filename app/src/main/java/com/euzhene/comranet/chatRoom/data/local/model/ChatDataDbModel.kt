package com.euzhene.comranet.chatRoom.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_data")
data class ChatDataDbModel(
    @PrimaryKey val timestamp: Long,
    val senderUsername: String,
    val owner: Boolean,
    val type:String,
    val data:String,
)