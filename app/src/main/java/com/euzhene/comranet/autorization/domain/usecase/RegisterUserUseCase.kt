package com.euzhene.comranet.autorization.domain.usecase

import com.euzhene.comranet.autorization.domain.entity.UserRegistrationData
import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val repo: AuthRepo) {
    suspend operator fun invoke(data: UserRegistrationData): Flow<Response<FirebaseUser>> {
        return repo.registerUser(data)
    }
}