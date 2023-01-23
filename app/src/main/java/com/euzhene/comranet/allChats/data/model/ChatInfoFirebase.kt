package com.euzhene.comranet.allChats.data.model

import androidx.annotation.Keep

@Keep
data class ChatInfoFirebase(
    val chat_id:String,
    val chat_name: String,
    val members: List<String>,
    val photo_url: String?,
) {
    constructor():this("","", emptyList(), null)
}

