package com.euzhene.comranet.allChats.data.model

import androidx.annotation.Keep

@Keep
data class ChatInfoFirebase(
    val chatId:String,
    val chatName: String,
    val members: List<String>,
    val chatPhoto: String?,
) {
    constructor():this("","", emptyList(), null)
}
