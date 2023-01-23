package com.euzhene.comranet.allChats.domain

import javax.inject.Inject

class ObserveChatsUseCase @Inject constructor(private val repo: AllChatsRepo) {
    suspend operator fun invoke() {
        return repo.observeNewChats()
    }
}