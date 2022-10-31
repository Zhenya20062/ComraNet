package com.euzhene.comranet.chatRoom.data.remote.dto

data class FirebaseChangeData constructor(
    val messageId:String,
    val data: String,
) {
    constructor() : this("", "")
}
