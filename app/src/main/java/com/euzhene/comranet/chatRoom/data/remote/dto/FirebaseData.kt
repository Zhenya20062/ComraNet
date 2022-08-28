package com.euzhene.comranet.chatRoom.data.remote.dto

class FirebaseData constructor(
    val timestamp: Long,
    val senderUsername: String,
    val senderId: String,
    val type: String,
    val data: String,
) {
    constructor() : this(0, "", "", "", "")
}
