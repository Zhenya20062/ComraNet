package com.euzhene.comranet.allChats

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.allChats.domain.GetAllChatsUseCase
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
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val observeChatsUseCase: ObserveChatsUseCase,
) : ViewModel() {
    private val _observedChatInfo = mutableStateListOf<ChatInfoWithId>()
    val observedChatInfo: List<ChatInfoWithId> = _observedChatInfo

    var pagingData = getAllChatsUseCase().cachedIn(viewModelScope)

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    init {
        viewModelScope.launch(Dispatchers.IO) {
                observeChatsUseCase().collectLatest {
                    _observedChatInfo.add(it)
                }
        }

    }
}