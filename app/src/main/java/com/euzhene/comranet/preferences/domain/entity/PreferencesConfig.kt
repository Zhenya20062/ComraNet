package com.euzhene.comranet.preferences.domain.entity

import androidx.annotation.Keep
import javax.inject.Inject

@Keep
data class PreferencesConfig @Inject constructor(
    val photoBackground: String?,
    val fontSize: Float,
    val chatTheme: ChatColorTheme,
)
