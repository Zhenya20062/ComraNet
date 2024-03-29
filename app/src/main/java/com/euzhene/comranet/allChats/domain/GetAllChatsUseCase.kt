package com.euzhene.comranet.allChats.domain

import androidx.paging.PagingData
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllChatsUseCase @Inject constructor (private val repo: AllChatsRepo) {
    operator fun invoke(): Flow<PagingData<ChatInfo>> {
        return repo.getAllChats()
    }
}