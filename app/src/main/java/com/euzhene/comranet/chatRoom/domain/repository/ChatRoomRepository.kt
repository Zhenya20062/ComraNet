package com.euzhene.comranet.chatRoom.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface ChatRoomRepository {
    fun getGroupInfo():Flow<Result<ChatInfo>>
    fun getChatData(): Flow<PagingData<ChatData>>
    suspend fun sendChatImage(imageUri: Uri): Flow<Response<Unit>>
    suspend fun sendChatMessage(message: String): Flow<Response<Unit>>
    suspend fun sendChatPoll(pollData: PollData):Flow<Response<Unit>>
    suspend fun changeChatPoll(chatData: ChatData): Flow<Response<Unit>>
    suspend fun observeNewChatData()
    suspend fun observeChangedChatData()

    fun setChatId(id:String)
}