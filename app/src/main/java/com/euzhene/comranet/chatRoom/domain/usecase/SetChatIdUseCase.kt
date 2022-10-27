package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.data.repository.ChatRoomRepositoryImpl
import javax.inject.Inject

class SetChatIdUseCase @Inject constructor (private val repo:ChatRoomRepositoryImpl) {
    operator fun invoke(chatId:String) {
        return repo.setChatId(chatId)
    }
}