package com.euzhene.comranet.chatRoom.presentation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.chatRoom.domain.usecase.*
import com.euzhene.comranet.preferences.data.PreferenceRepoImpl
import com.euzhene.comranet.preferences.domain.usecase.GetConfigUseCase
import com.euzhene.comranet.util.Response
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    val stateHandle: SavedStateHandle,
    getChatDataUseCase: GetChatDataUseCase,
    private val sendChatImageUseCase: SendChatImageUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val sendChatPollUseCase: SendPollUseCase,
    private val changePollUseCase: ChangePollUseCase,
    private val observeNewChatDataUseCase: ObserveNewChatDataUseCase,
    private val observeChangedChatDataUseCase: ObserveChangedChatDataUseCase,
    getConfigUseCase: GetConfigUseCase,
    setChatIdUseCase: SetChatIdUseCase,
) : ViewModel() {
    //val observedChatData = mutableStateListOf<ChatData>()

    var config by mutableStateOf(PreferenceRepoImpl.defaultConfig)

    private val _chatError = mutableStateOf("")
    val chatError: State<String> = _chatError

    private val _chatDataLoading = mutableStateOf(false)
    val chatDataLoading: State<Boolean> = _chatDataLoading

    init {
        setChatIdUseCase(stateHandle.get<String>(CHAT_ID_STATE)!!)
        observeChatData()

        viewModelScope.launch(Dispatchers.Main) {
            getConfigUseCase().collectLatest {
                config = it
            }
        }

    }

    val chatDataPaging = getChatDataUseCase().cachedIn(viewModelScope)

    fun sendPoll(pollData: PollData) {
        viewModelScope.launch(Dispatchers.IO) {
            sendChatPollUseCase(pollData).collectLatest {
                handleChatDataState(it)
            }
        }
    }

    init {
//        sendPoll(
//            PollData(
//                heading = "Facts and statistics",
//                options = listOf(
//                    Pair("1", 2), Pair("2", 3), Pair("3", 1)
//                ),
//                yourChoice = 0,
//            )
//        )
    }

    fun changePoll(chatData: ChatData, option: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pollData = Gson().fromJson(chatData.data, PollData::class.java)
            val changedOptions = pollData.options.toMutableList().apply {
                val oldPair = get(option)
                removeAt(option)
                add(option, Pair(oldPair.first, oldPair.second + 1))
                if (pollData.yourChoice != null) {
                    val previousVote = get(pollData.yourChoice)
                    this[pollData.yourChoice] = Pair(previousVote.first, previousVote.second - 1)
                }
            }
            val newData = pollData.copy(
                yourChoice = option,
                options = changedOptions
            )

            changePollUseCase(chatData.copy(data = Gson().toJson(newData))).collectLatest {
                handleChatDataState(it)
            }
        }
    }

    fun sendImage(imgUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            sendChatImageUseCase(imgUri).collectLatest {
                handleChatDataState(it)
            }
        }
    }

    private fun handleChatDataState(res: Response<Unit>) {
        when (res) {
            is Response.Loading -> {
                _chatDataLoading.value = true
            }
            is Response.Error -> {
                _chatDataLoading.value = false
                _chatError.value = res.error!!
            }
            is Response.Success -> {
                _chatDataLoading.value = false
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (message.isBlank()) return@launch

            sendChatMessageUseCase(message.trim()).collectLatest {
                handleChatDataState(it)
            }
        }
    }

    private fun observeChatData() {
        observeChangedChatDataUseCase()
        observeNewChatDataUseCase()
    }

    companion object {
        const val CHAT_ID_STATE = "chat_id"
    }
}

