package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendPollUseCase @Inject constructor(private val repo: ChatRoomRepository) {
    suspend operator fun invoke(pollData: PollData): Flow<Response<Unit>> {
        return repo.sendChatPoll(pollData)
    }
}