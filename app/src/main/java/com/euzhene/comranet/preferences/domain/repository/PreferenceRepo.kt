package com.euzhene.comranet.preferences.domain.repository

import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import kotlinx.coroutines.flow.Flow

interface PreferenceRepo {
    fun getPreferences(): Flow<PreferencesConfig>
    suspend fun updatePreferences(config: PreferencesConfig)
}