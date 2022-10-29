package com.euzhene.comranet.allChats.data.model

data class ChatInfoFirebase(
    val chatId:String,
    val chatName: String,
    val members: List<String>,
    val chatPhoto: String?,
) {
    constructor():this("","", emptyList(), null)
}
