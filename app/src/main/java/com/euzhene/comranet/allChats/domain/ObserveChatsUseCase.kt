package com.euzhene.comranet.allChats.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatsUseCase @Inject constructor(private val repo: AllChatsRepo) {
    operator fun invoke(): Flow<ChatInfoWithId> {
        return repo.observeChats()
    }
}