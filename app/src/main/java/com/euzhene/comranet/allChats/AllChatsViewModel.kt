package com.euzhene.comranet.allChats

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.allChats.domain.GetAllChatsUseCase
import com.euzhene.comranet.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllChatsViewModel @Inject constructor(
    private val getAllChatsUseCase: GetAllChatsUseCase
) : ViewModel() {
    private val _chatInfoList = mutableStateListOf<ChatInfoWithId>()
    val chatInfoList: List<ChatInfoWithId> = _chatInfoList

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage:SharedFlow<String> = _errorMessage

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllChatsUseCase().collectLatest {
                when (it) {
                    is Response.Success -> _chatInfoList.addAll(it.data!!)
                    is Response.Error -> {
                        _errorMessage.emit(it.error!!)
                    }
                    is Response.Loading -> {}
                }
            }
        }

    }
}