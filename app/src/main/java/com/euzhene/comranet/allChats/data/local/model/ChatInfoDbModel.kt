package com.euzhene.comranet.allChats.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_info")
data class ChatInfoDbModel(
    @PrimaryKey
    val chatId:String,
    val chatName: String,
    val members: List<String>,
    val chatPhoto: String?,
)
