package com.euzhene.comranet.autorization.domain.usecase

import com.euzhene.comranet.autorization.domain.entity.UserLoginData
import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val repo: AuthRepo) {
    suspend operator fun invoke(data: UserLoginData): Flow<Response<FirebaseUser>> {
        return repo.logInUser(data)
    }
}