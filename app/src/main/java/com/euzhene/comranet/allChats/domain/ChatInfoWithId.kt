package com.euzhene.comranet.allChats.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo

data class ChatInfoWithId(
    val chatInfo: ChatInfo,
    val chatId: String,
)