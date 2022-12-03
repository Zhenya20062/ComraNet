package com.euzhene.comranet.chatRoom.data.remote.dto

import androidx.annotation.Keep

@Keep
data class FirebaseChangeData constructor(
    val messageId:String,
    val data: String,
) {
    constructor() : this("", "")
}
