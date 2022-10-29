package com.euzhene.comranet.allChats.data.paging

import androidx.paging.PagingData
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import kotlinx.coroutines.flow.Flow

interface PagingDataSource {
    fun getChatData(): Flow<PagingData<ChatInfoDbModel>>
}