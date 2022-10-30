package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.util.Response
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    suspend operator fun invoke(chatMessage: String): Flow<Response<Unit>> {
        return repository.sendChatMessage(chatMessage)
    }
}