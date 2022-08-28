package com.euzhene.comranet.autorization.domain.usecase

import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class IsSignInUseCase @Inject constructor(private val repo: AuthRepo) {
    operator fun invoke(): FirebaseUser? {
        return repo.isSignIn()
    }
}