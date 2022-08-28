package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatDataUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    operator fun invoke(): Flow<ChatData> {
        return repository.observeChatData()
    }
}