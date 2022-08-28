package com.euzhene.comranet.chatRoom.data.paging

import androidx.paging.PagingData
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import kotlinx.coroutines.flow.Flow

interface PagingDataSource {
    fun getChatData(): Flow<PagingData<ChatDataDbModel>>
    fun setUserId(id:String)
}