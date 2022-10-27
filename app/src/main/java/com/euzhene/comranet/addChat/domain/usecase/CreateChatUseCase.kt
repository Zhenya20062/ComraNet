package com.euzhene.comranet.addChat.domain.usecase

import com.euzhene.comranet.addChat.domain.AddChatRepo
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateChatUseCase @Inject constructor (private val repo: AddChatRepo) {
    operator fun invoke(chatInfo: ChatInfo,  userLogins:List<String>): Flow<Response<Unit>> {
        return  repo.createChat(chatInfo, userLogins)
    }
}