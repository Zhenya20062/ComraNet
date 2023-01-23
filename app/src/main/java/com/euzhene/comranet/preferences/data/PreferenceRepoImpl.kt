package com.euzhene.comranet.preferences.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.euzhene.comranet.chatRoom.presentation.theme.defaultTheme
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.domain.repository.PreferenceRepo
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceRepo {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    private val dataStore = context.dataStore

    override fun getPreferences(): Flow<PreferencesConfig> {
        return dataStore.data.map {
            it[stringPreferencesKey(CONFIG_NAME)]
        }.map {
            if (it == null) defaultConfig
            else Gson().fromJson(it, PreferencesConfig::class.java)
        }

    }

    override suspend fun updatePreferences(config: PreferencesConfig) {
        val json = Gson().toJson(config)
        dataStore.edit {
            it[stringPreferencesKey(CONFIG_NAME)] = json
        }
    }

    companion object {
        const val DATA_STORE_NAME = "settings"
        const val CONFIG_NAME = "config"
        private const val FONT_SIZE_DEFAULT_VALUE = 19f
        val defaultConfig = PreferencesConfig(
            null, FONT_SIZE_DEFAULT_VALUE, defaultTheme
        )
    }
}

