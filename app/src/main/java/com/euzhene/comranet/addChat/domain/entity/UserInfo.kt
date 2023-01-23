package com.euzhene.comranet.addChat.domain.entity

import androidx.annotation.Keep

@Keep
data class UserInfo(
    val username: String,
    val login: String,
    val photo_url: String?
) {
    constructor():this("","",null)
}