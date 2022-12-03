package com.euzhene.comranet.preferences.domain.entity

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import javax.inject.Inject

@Keep
data class PreferencesConfig @Inject constructor(
    val background: String?,
    val fontSize: Float,

    val colorOfReceiverMessage: Color,
    val colorOfSenderMessage: Color,
    val colorOfAppBar: Color,
    val colorOfIconSection: Color,
    val colorOfMessageUsername: Color,
    val colorOfMessageText: Color,
    val colorOfMessageDate: Color,
    val colorOfDateDividerBackground: Color,
    val colorOfDateDividerText: Color,
)
