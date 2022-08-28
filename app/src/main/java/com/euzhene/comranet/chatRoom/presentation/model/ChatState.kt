package com.euzhene.comranet.chatRoom.presentation.model

import com.euzhene.comranet.chatRoom.domain.entity.ChatData

data class ChatState(
    val chatDataList:List<ChatData> = emptyList(),
    val isLoading:Boolean = false,
)
