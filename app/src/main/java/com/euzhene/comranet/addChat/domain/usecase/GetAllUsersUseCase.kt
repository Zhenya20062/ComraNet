package com.euzhene.comranet.addChat.domain.usecase

import com.euzhene.comranet.addChat.domain.AddChatRepo
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor (private val repo:AddChatRepo) {
    suspend operator fun invoke(): List<UserInfo> {
        return repo.getAllUsers()
    }
}