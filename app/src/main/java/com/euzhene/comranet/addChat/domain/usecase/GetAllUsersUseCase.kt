package com.euzhene.comranet.addChat.domain.usecase

import com.euzhene.comranet.addChat.domain.AddChatRepo
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor (private val repo:AddChatRepo) {
    suspend operator fun invoke(): Flow<Response<List<UserInfo>>> {
        return repo.getAllUsers()
    }
}