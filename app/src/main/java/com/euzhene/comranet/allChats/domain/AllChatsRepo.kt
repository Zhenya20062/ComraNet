package com.euzhene.comranet.allChats.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface AllChatsRepo {
    fun getAllChats(): Flow<Response<List<ChatInfoWithId>>>
}