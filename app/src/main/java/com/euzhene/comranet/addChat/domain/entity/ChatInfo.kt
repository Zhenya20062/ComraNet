package com.euzhene.comranet.addChat.domain.entity

import android.net.Uri

data class ChatInfo(
    val chatName:String,
    val members:List<String>,
    val chatUri: Uri?,
)
