package com.euzhene.comranet.chatRoom.presentation

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.chatRoom.domain.usecase.*
import com.euzhene.comranet.chatRoom.presentation.component.DateItem
import com.euzhene.comranet.preferences.data.PreferenceRepoImpl
import com.euzhene.comranet.preferences.domain.usecase.GetConfigUseCase
import com.euzhene.comranet.util.D_MMM_YYYY
import com.euzhene.comranet.util.Response
import com.euzhene.comranet.util.datesEqual
import com.euzhene.comranet.util.mapTimestampToDate
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getChatDataUseCase: GetChatDataUseCase,
    private val sendChatImageUseCase: SendChatImageUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val sendChatPollUseCase: SendPollUseCase,
    private val changePollUseCase: ChangePollUseCase,
    private val observeNewChatDataUseCase: ObserveNewChatDataUseCase,
    private val observeChangedChatDataUseCase: ObserveChangedChatDataUseCase,
    private val getGroupInfoUseCase: GetGroupInfoUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val setChatIdUseCase: SetChatIdUseCase,
) : ViewModel() {
    var chatId: String
        private set

    var config by mutableStateOf(PreferenceRepoImpl.defaultConfig)

    private val _groupInfo = mutableStateOf<ChatInfo?>(null)
    val groupInfo: State<ChatInfo?> = _groupInfo

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _chatDataLoading = mutableStateOf(false)
    val chatDataLoading: State<Boolean> = _chatDataLoading

    init {
        chatId = savedStateHandle.get<String>("chatId") ?: throw RuntimeException("chat id not found")
        getData()
    }

    var chatDataPaging = getChatDataUseCase()
        .map {
            it.insertSeparators { before: ChatData?, after: ChatData? ->
                if (before == null) {
                    return@insertSeparators null
                } else if (after == null) {
                    return@insertSeparators DateItem(
                        mapTimestampToDate(before.timestamp, D_MMM_YYYY)
                    )
                } else {
                    return@insertSeparators if (datesEqual(before.timestamp,after.timestamp, Calendar.DAY_OF_YEAR)) null
                    else DateItem(mapTimestampToDate(before.timestamp, D_MMM_YYYY))
                }
            }
        }
        .cachedIn(viewModelScope)


    private fun getData() {
        setChatIdUseCase(chatId)
        observeChatData()

        viewModelScope.launch(Dispatchers.Main) {
            getConfigUseCase().collectLatest {
                config = it
            }
        }
     //   getGroupInfo()
    }

    private fun getGroupInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            getGroupInfoUseCase().collectLatest {
                if (it.isSuccess) {
                    _groupInfo.value = it.getOrNull()
                } else {
                    _errorMessage.value = it.exceptionOrNull()?.message ?: "Group info error"
                }
            }
        }
    }


    fun sendPoll(pollData: PollData) {
        viewModelScope.launch(Dispatchers.IO) {
            sendChatPollUseCase(pollData).collectLatest {
                handleChatDataState(it)
            }
        }
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
                _errorMessage.value = res.error!!
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
//        viewModelScope.launch(Dispatchers.IO) {
//            observeChangedChatDataUseCase()
//        }
        viewModelScope.launch(Dispatchers.IO) {
            observeNewChatDataUseCase()
        }
    }

    companion object {
        const val CHAT_ID_STATE = "chat_id"
    }
}

