package com.euzhene.comranet.addChat.data

data class ChatInfoFirebase(
    val chatName:String,
    val members:List<String>,
    val chatPhoto:String?
)