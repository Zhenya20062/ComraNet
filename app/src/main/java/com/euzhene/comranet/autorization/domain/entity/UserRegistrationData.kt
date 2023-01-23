package com.euzhene.comranet.autorization.domain.entity

import android.net.Uri
import androidx.annotation.Keep

data class UserRegistrationData(
    val email: String,
    val password: String,
    val login: String,
    val username: String,
    val photoUri: Uri?,
)
@Keep
data class UserInfoFirestore(
    val email: String,
    val login: String,
    val photo_url: String?,
    val username: String,
    val notification_id: String,
)