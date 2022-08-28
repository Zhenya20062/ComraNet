package com.euzhene.comranet.autorization.domain.entity

import android.net.Uri

data class UserRegistrationData(
    val email: String,
    val password: String,
    val login: String,
    val username: String,
    val photoUri: Uri?,
)
