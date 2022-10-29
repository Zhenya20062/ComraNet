package com.euzhene.comranet.allChats.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_info_remote_keys")
data class ChatInfoRemoteKeysDbModel(
    @PrimaryKey
    val chatId:String,
    val prev:String?,
    val next:String?,
)