package com.euzhene.comranet.addChat.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface AddChatRepo {
    fun createChat(chatInfo: ChatInfo): Flow<Response<Unit>>
    suspend fun getAllUsers(): List<UserInfo>
}