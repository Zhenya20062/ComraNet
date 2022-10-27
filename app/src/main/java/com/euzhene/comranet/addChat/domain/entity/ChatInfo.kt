package com.euzhene.comranet.addChat.domain.entity

data class ChatInfo(
    val chatName: String,
    val members: List<String>,
    val chatPhoto: String?,
) {
    constructor() : this("", emptyList(), null)
}

