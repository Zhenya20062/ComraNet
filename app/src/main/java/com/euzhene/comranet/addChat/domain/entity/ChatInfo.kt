package com.euzhene.comranet.addChat.domain.entity

import androidx.annotation.Keep

@Keep
data class ChatInfo(
    val chat_id: String,
    val chat_name: String,
    val members: List<String>,
    val photo_url: String?,
) {
    constructor() : this("", "", emptyList(), null)
}

@Keep
data class ChatInfoSendFirestore(
    val chat_name: String,
    val photo_url: String?,
) {
    constructor() : this("", null)
}

@Keep
data class ChatMember(
    val chat_id: String,
    val user_id: String,
) {
    constructor() : this("", "")
}
