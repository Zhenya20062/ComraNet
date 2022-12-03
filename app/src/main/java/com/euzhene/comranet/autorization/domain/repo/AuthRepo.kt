package com.euzhene.comranet.autorization.domain.repo

import com.euzhene.comranet.autorization.domain.entity.UserLoginData
import com.euzhene.comranet.autorization.domain.entity.UserRegistrationData
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepo {
    suspend fun registerUser(data: UserRegistrationData): Flow<Response<FirebaseUser>>
    suspend fun logInUser(data: UserLoginData): Flow<Response<FirebaseUser>>
    fun isSignIn(): FirebaseUser?
    suspend fun updateNotificationId(): Response<Unit>
}