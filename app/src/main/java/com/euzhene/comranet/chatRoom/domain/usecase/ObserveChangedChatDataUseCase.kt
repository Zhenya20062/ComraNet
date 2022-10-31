package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChangedChatDataUseCase @Inject constructor(private val repo:ChatRoomRepository) {
    operator fun invoke() {
        return repo.observeChangedChatData()
    }
}