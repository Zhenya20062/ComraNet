package com.euzhene.comranet.chatRoom.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class FirebaseDataModel constructor(
    val message_id: String,
    val timestamp: Timestamp,
    val chat_id: String,
    val sender_id: String,
    val type: String,
    val data: String,
    val senderName: String,
) {
    constructor() : this("", Timestamp(0,0), "", "", "", "", "")
}
