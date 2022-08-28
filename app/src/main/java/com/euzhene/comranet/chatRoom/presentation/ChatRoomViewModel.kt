package com.euzhene.comranet.chatRoom.presentation

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.usecase.*
import com.euzhene.comranet.preferences.data.PreferenceRepoImpl
import com.euzhene.comranet.preferences.domain.usecase.GetConfigUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class ChatRoomViewModel @AssistedInject constructor(
    getChatDataUseCase: GetChatDataUseCase,
    private val sendChatImageUseCase: SendChatImageUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val observeChatDataUseCase: ObserveChatDataUseCase,
    getConfigUseCase: GetConfigUseCase,
    setUserUseCase: SetUserUseCase,
    @Assisted user: FirebaseUser,
) : ViewModel() {
    val observedChatData = mutableStateListOf<ChatData>()

    var config by mutableStateOf(PreferenceRepoImpl.defaultConfig)

    init {
        setUserUseCase(user)
        observeChatData()

        viewModelScope.launch(Dispatchers.Main) {
            getConfigUseCase().collectLatest {
                config = it
            }
        }

    }
    val chatDataPaging = getChatDataUseCase().cachedIn(viewModelScope)



    fun sendImage(imgUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = sendChatImageUseCase(imgUri)
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (message.isBlank()) return@launch

            val response = sendChatMessageUseCase(message.trim())
        }
    }

    private fun observeChatData() {
        viewModelScope.launch(Dispatchers.IO) {
            observeChatDataUseCase()
                .collect {
                    observedChatData.add(0, it)
                }
        }

    }
}

