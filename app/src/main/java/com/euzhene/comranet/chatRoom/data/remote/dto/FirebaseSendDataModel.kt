package com.euzhene.comranet.chatRoom.data.remote.dto

import androidx.annotation.Keep
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.google.firebase.Timestamp
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp

@Keep
data class FirebaseSendDataModel(

    val chat_id: String,
    @ServerTimestamp val timestamp: Timestamp?=null,
    val sender_id: String,
    val type: ChatDataType,
    val data: Any,
)
