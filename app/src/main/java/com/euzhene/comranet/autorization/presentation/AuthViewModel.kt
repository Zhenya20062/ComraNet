package com.euzhene.comranet.autorization.presentation

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euzhene.comranet.autorization.domain.entity.UserLoginData
import com.euzhene.comranet.autorization.domain.entity.UserRegistrationData
import com.euzhene.comranet.autorization.domain.usecase.IsSignInUseCase
import com.euzhene.comranet.autorization.domain.usecase.LoginUserUseCase
import com.euzhene.comranet.autorization.domain.usecase.RegisterUserUseCase
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val isSignInUseCase: IsSignInUseCase,
) : ViewModel() {
    var emailOrLogin by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var login by mutableStateOf("")
    var username by mutableStateOf("")
    var photo by mutableStateOf<Uri?>(null)

    private var _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private var _shouldShowDialog = mutableStateOf(false)
    val shouldShowDialog: State<Boolean> = _shouldShowDialog

    private var _shouldGoToChatRoom = mutableStateOf(false)
    val shouldGoToChatRoom: State<Boolean> = _shouldGoToChatRoom

    private var _userInfo = mutableStateOf<FirebaseUser?>(null)
    val userInfo: State<FirebaseUser?> = _userInfo

    init {
        checkAuth()
    }

    fun registerUser() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!registerChecks()) return@launch

            val userRegistrationData = UserRegistrationData(email, password, login, username, photo)
            registerUserUseCase(userRegistrationData).collectLatest {
                when (it) {
                    is Response.Error -> {
                        _shouldShowDialog.value = false
                        _toastMessage.emit(it.error.toString())
                    }
                    is Response.Loading -> {
                        _shouldShowDialog.value = true
                    }
                    is Response.Success -> {
                        onResponseSuccess(it)
                    }
                }
            }

        }
    }

    fun loginUser() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!loginChecks()) return@launch

            val userLoginData = if (emailOrLogin.contains('@'))
                UserLoginData(email = emailOrLogin, password = password, login = null)
            else UserLoginData(email = null, password = password, login = emailOrLogin)
            loginUserUseCase(userLoginData).collectLatest {
                when (it) {
                    is Response.Error -> {
                        _shouldShowDialog.value = false
                        _toastMessage.emit(it.error.toString())
                    }
                    is Response.Loading -> {
                        _shouldShowDialog.value = true
                    }
                    is Response.Success -> {
                        onResponseSuccess(it)
                    }
                }
            }

        }
    }

    private fun checkAuth() {
        val user = isSignInUseCase() ?: return

        _userInfo.value = user
        _shouldGoToChatRoom.value = true
    }

    private suspend fun registerChecks(): Boolean {
        if (email.isBlank()) {
            _toastMessage.emit("Email is empty")
            return false
        }
        if (password.isBlank()) {
            _toastMessage.emit("Password is empty")
            return false
        }
        if (login.isBlank()) {
            _toastMessage.emit("Login is empty")
        }
        if (username.isBlank()) {
            _toastMessage.emit("Username is empty")
        }
        login = login.trim()
        email = email.trim()
        return true
    }

    private suspend fun loginChecks(): Boolean {
        if (emailOrLogin.isBlank()) {
            _toastMessage.emit("Email/login is empty")
            return false
        }
        if (password.isBlank()) {
            _toastMessage.emit("Password is empty")
            return false
        }
        emailOrLogin = emailOrLogin.trim()
        return true
    }

    private fun onResponseSuccess(response: Response.Success<FirebaseUser>) {
        _shouldShowDialog.value = false
        _userInfo.value = response.data
        _shouldGoToChatRoom.value = true
    }
}