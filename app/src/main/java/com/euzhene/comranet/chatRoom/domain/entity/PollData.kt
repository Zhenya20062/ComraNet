package com.euzhene.comranet.chatRoom.domain.entity

data class PollData(
    val heading:String,
    val options:List<Pair<String, Int>>,
    val yourChoice:Int?,
)
