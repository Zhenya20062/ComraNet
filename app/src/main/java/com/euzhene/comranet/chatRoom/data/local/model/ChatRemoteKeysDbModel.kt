package com.euzhene.comranet.chatRoom.data.local.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "chat_remote_keys")
data class ChatRemoteKeysDbModel(
    @PrimaryKey
    val timestamp:Long,
    val prev:Long?,
    val next:Long?,
    val chatId:String,
    )