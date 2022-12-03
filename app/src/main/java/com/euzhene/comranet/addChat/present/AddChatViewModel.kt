package com.euzhene.comranet.addChat.present

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.addChat.domain.usecase.CreateChatUseCase
import com.euzhene.comranet.addChat.domain.usecase.GetAllUsersUseCase
import com.euzhene.comranet.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChatViewModel @Inject constructor(
    private val createChatUseCase: CreateChatUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
) : ViewModel() {

    private val _includedUsers = mutableStateListOf<UserInfo>()
    val includedUsers: List<UserInfo> = _includedUsers

    private val _allUsers = mutableStateOf<List<UserInfo>>(emptyList())
    val allUsers: State<List<UserInfo>> = _allUsers

    var chatName by mutableStateOf("")
    var chatUri by mutableStateOf<Uri?>(null)

    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private var _success = mutableStateOf(false)
    val success: State<Boolean> = _success

    private var _error = mutableStateOf("")
    val error: State<String> = _error

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _allUsers.value = getAllUsersUseCase()
        }
    }

    fun addNewMember(userInfo: UserInfo) {
        _includedUsers.add(userInfo)
    }

    fun removeMember(userInfo: UserInfo) {
        _includedUsers.remove(userInfo)
    }

    fun createChat() {
        viewModelScope.launch(Dispatchers.IO) {
            val chatInfo = ChatInfo(
                chatName,
                _includedUsers.map { it.login },
                if (chatUri == null) null else chatUri.toString()
            )

            createChatUseCase(chatInfo, _includedUsers.map { it.login }).collectLatest {
                when (it) {
                    is Response.Success -> {
                        _isLoading.value = false
                        _success.value = true
                    }
                    is Response.Loading -> _isLoading.value = true
                    is Response.Error -> {
                        _isLoading.value = false
                        _error.value = it.error!!
                    }
                }
            }
        }

    }
}