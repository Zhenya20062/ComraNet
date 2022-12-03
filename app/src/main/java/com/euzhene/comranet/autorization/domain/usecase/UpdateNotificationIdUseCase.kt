package com.euzhene.comranet.autorization.domain.usecase

import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNotificationIdUseCase @Inject constructor(
    private val repo:AuthRepo
) {
    suspend operator fun invoke(): Response<Unit> {
        return repo.updateNotificationId()
    }
}