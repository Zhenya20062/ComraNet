package com.euzhene.comranet.preferences.domain.usecase

import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.domain.repository.PreferenceRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(
    private val repo: PreferenceRepo
) {
    operator fun invoke(): Flow<PreferencesConfig> {
        return repo.getPreferences()
    }
}