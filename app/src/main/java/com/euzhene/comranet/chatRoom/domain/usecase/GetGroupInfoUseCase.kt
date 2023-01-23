package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupInfoUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    operator fun invoke() : Flow<Result<ChatInfo>> {
        return repository.getGroupInfo()
    }
}