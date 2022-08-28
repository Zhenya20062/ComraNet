package com.euzhene.comranet.preferences.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.euzhene.comranet.*
import com.euzhene.comranet.preferences.domain.entity.PreferencesConfig
import com.euzhene.comranet.preferences.domain.repository.PreferenceRepo
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
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
        val defaultConfig = PreferencesConfig(
            background = null,
            fontSize = FONT_SIZE_DEFAULT_VALUE,
            colorOfReceiverMessage = RECEIVER_MESSAGE_VALUE,
            colorOfSenderMessage = SENDER_MESSAGE_VALUE,
            colorOfAppBar = APP_BAR_VALUE,
            colorOfIconSection = ICON_SECTION_VALUE,
            colorOfMessageUsername = MESSAGE_USERNAME_VALUE,
            colorOfMessageText = MESSAGE_TEXT_VALUE,
            colorOfMessageDate = MESSAGE_DATE_VALUE,
            colorOfDateDividerBackground = DATE_DIVIDER_BACKGROUND_VALUE,
            colorOfDateDividerText = DATE_DIVIDER_TEXT_VALUE,
        )
    }
}

