package com.euzhene.comranet.allChats.domain

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllChatsUseCase @Inject constructor (private val repo: AllChatsRepo) {
    operator fun invoke(): Flow<Response<List<ChatInfoWithId>>> {
        return repo.getAllChats()
    }
}