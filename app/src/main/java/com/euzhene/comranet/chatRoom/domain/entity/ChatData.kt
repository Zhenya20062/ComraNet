package com.euzhene.comranet.chatRoom.domain.entity

import javax.inject.Inject


data class ChatData @Inject constructor(
    val messageId:String,
    val timestamp: Long,
    val senderUsername: String,
    val owner: Boolean,
    val type: ChatDataType,
    val data: String,
)