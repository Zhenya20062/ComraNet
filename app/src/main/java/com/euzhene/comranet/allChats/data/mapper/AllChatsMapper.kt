package com.euzhene.comranet.allChats.data.mapper

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.model.ChatInfoFirebase
import javax.inject.Inject

class AllChatsMapper @Inject constructor() {
    fun mapDtoToDbModel(dto: ChatInfoFirebase):ChatInfoDbModel {
        return ChatInfoDbModel(
            chatId = dto.chatId,
            chatName = dto.chatName,
            members = dto.members,
            chatPhoto = dto.chatPhoto,
        )
    }

}