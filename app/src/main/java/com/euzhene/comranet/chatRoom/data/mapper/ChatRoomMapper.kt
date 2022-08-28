package com.euzhene.comranet.chatRoom.data.mapper

import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import javax.inject.Inject

class ChatRoomMapper @Inject constructor() {
    fun mapDtoToEntity(firebaseData: FirebaseData, userId: String): ChatData {
        return ChatData(
            firebaseData.timestamp,
            firebaseData.senderUsername,
            firebaseData.senderId == userId,
            ChatDataType.valueOf(firebaseData.type),
            firebaseData.data
        )
    }

    fun mapDtoToDbModel(firebaseData: FirebaseData, userId: String): ChatDataDbModel {
        return ChatDataDbModel(
            timestamp = firebaseData.timestamp,
            senderUsername = firebaseData.senderUsername,
            owner = firebaseData.senderId == userId,
            type = firebaseData.type,
            data = firebaseData.data
        )
    }

    fun mapDbModelToEntity(dbModel: ChatDataDbModel): ChatData {
        return ChatData(
            timestamp = dbModel.timestamp,
            senderUsername = dbModel.senderUsername,
            owner = dbModel.owner,
            type = ChatDataType.valueOf(dbModel.type),
            data = dbModel.data
        )
    }
}