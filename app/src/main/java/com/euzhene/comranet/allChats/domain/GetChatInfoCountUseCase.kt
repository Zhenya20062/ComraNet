package com.euzhene.comranet.allChats.domain

import javax.inject.Inject

class GetChatInfoCountUseCase @Inject constructor(private val repo: AllChatsRepo) {
    suspend operator fun invoke(): Int {
        return repo.getChatInfoCount()
    }
}