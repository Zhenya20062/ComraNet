package com.euzhene.comranet.autorization.domain.entity

data class UserLoginData(
    val email: String? = null,
    val password: String,
    val login: String? = null,
)
