package com.euzhene.comranet.allChats.domain

import androidx.paging.PagingData
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface AllChatsRepo {
    fun observeChats(): Flow<ChatInfoWithId>
    fun getAllChats(): Flow<PagingData<ChatInfoWithId>>
}