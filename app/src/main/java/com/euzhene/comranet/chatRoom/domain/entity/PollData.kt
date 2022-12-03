package com.euzhene.comranet.chatRoom.domain.entity

import androidx.annotation.Keep

@Keep
data class PollData(
    val heading:String,
    val options:List<Pair<String, Int>>,
    val yourChoice:Int?,
)
