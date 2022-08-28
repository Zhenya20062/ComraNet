package com.euzhene.comranet.chatRoom.domain.usecase

import com.euzhene.comranet.chatRoom.data.repository.ChatRoomRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SetUserUseCase @Inject constructor(
    private val repo: ChatRoomRepositoryImpl
) {
    operator fun invoke(user: FirebaseUser) {
        return repo.setUser(user)
    }
}