package com.euzhene.comranet.preferences.domain.usecase

import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.domain.repository.PreferenceRepo
import javax.inject.Inject

class UpdateConfigUseCase @Inject constructor(private val repo: PreferenceRepo) {
    suspend operator fun invoke(config: PreferencesConfig) {
        return repo.updatePreferences(config)
    }
}