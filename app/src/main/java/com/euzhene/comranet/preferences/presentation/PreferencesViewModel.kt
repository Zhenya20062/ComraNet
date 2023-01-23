package com.euzhene.comranet.preferences.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euzhene.comranet.preferences.data.PreferenceRepoImpl
import com.euzhene.comranet.preferences.domain.usecase.GetConfigUseCase
import com.euzhene.comranet.preferences.domain.usecase.UpdateConfigUseCase
import com.euzhene.comranet.preferences.presentation.ColorCustomizer.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase,
    private val updateConfigUseCase: UpdateConfigUseCase,
) : ViewModel() {
    var config by mutableStateOf(PreferenceRepoImpl.defaultConfig)

    private val _preferencesSaved = mutableStateOf(false)
    val preferencesSaved: State<Boolean> = _preferencesSaved


    init {
        viewModelScope.launch(Dispatchers.IO) {
            getConfigUseCase().collect {
                withContext(Dispatchers.Main) {
                    config = it
                }
            }
        }
    }

    fun saveConfigAndExit() {
        viewModelScope.launch(Dispatchers.IO) {
            updateConfigUseCase(config)
            _preferencesSaved.value = true
        }
    }

//    fun updateColor(value: Color, colorCustomizer: ColorCustomizer) {
//        when (colorCustomizer) {
//            YOUR_MESSAGE -> config = config.copy(colorOfReceiverMessage = value)
//            SENDER_MESSAGE -> config.copy(colorOfSenderMessage = value).also { config = it }
//            APP_BAR -> config = config.copy(colorOfAppBar = value)
//            ICON_SECTION -> config = config.copy(colorOfIconSection = value)
//            MESSAGE_USERNAME -> config = config.copy(colorOfMessageUsername = value)
//            MESSAGE_TEXT -> config = config.copy(colorOfMessageText = value)
//            MESSAGE_DATE -> config = config.copy(colorOfMessageDate = value)
//            DATE_DIVIDER_BACKGROUND -> config = config.copy(colorOfDateDividerBackground = value)
//            DATE_DIVIDER_TEXT -> config = config.copy(colorOfDateDividerText = value)
//            else -> {}
//        }
//    }

}