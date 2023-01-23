package com.euzhene.comranet.allChats.pres

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.GetAllChatsUseCase
import com.euzhene.comranet.allChats.domain.GetChatInfoCountUseCase
import com.euzhene.comranet.allChats.domain.ObserveChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllChatsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getAllChatsUseCase: GetAllChatsUseCase,
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val getChatInfoCountUseCase: GetChatInfoCountUseCase,
) : ViewModel() {

    var pagingData = getAllChatsUseCase().cachedIn(viewModelScope)

    private var _chatInfoCount = mutableStateOf(NO_RESPONSE_FROM_DB_VALUE)
    val chatInfoCount:State<Int> = _chatInfoCount

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    init {
        viewModelScope.launch(Dispatchers.IO) {
            observeChatsUseCase()
        }
        viewModelScope.launch(Dispatchers.Main) {
           _chatInfoCount.value = getChatInfoCountUseCase()
        }
    }

    companion object {
        const val NO_RESPONSE_FROM_DB_VALUE = -1
    }
}