package com.euzhene.comranet.chatRoom.data.remote.dto

import androidx.annotation.Keep
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.google.firebase.database.ServerValue

data class FirebaseSendData(
    val messageId:String?=null,
    val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP,
    val senderUsername: String,
    val senderId:String,
    val type:ChatDataType,
    val data:Any,
)
