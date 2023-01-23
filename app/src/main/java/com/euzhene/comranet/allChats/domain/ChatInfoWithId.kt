package com.euzhene.comranet.allChats.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo

//todo: get rid of this class and add chatId field in ChatInfo class instead
data class ChatInfoWithId(
    val chatInfo: ChatInfo,
    val chatId: String,
)