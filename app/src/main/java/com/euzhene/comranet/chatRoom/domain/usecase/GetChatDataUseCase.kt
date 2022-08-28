package com.euzhene.comranet.chatRoom.domain.usecase

import androidx.paging.PagingData
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatDataUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    operator fun invoke() : Flow<PagingData<ChatData>> {
        return repository.getChatData()
    }
}