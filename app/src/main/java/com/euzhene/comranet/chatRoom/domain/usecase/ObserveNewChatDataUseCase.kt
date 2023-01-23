package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNewChatDataUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    suspend operator fun invoke() {
        return repository.observeNewChatData()
    }
}