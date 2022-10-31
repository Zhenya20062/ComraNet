package com.euzhene.comranet.chatRoom.data.mapper

import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class ChatRoomMapper @Inject constructor() {
    fun mapEntityToDto(type: ChatDataType, data: String, user: FirebaseUser): FirebaseSendData {
        return FirebaseSendData(
            senderUsername = user.displayName!!,
            senderId = user.uid,
            type = type,
            data = data,
        )
    }

    fun mapEntityToDto(chatData: ChatData): FirebaseChangeData {
        return FirebaseChangeData(
            messageId = chatData.messageId,
            data = chatData.data,
        )
    }

    fun mapDtoToEntity(firebaseData: FirebaseData, userId: String): ChatData {
        return ChatData(
            messageId = firebaseData.messageId,
            timestamp = firebaseData.timestamp,
            senderUsername = firebaseData.senderUsername,
            owner = firebaseData.senderId == userId,
            type = ChatDataType.valueOf(firebaseData.type),
            data = firebaseData.data,
        )
    }

    fun mapDtoToDbModel(
        firebaseData: FirebaseData,
        userId: String,
        chatId: String
    ): ChatDataDbModel {
        return ChatDataDbModel(
            timestamp = firebaseData.timestamp,
            senderUsername = firebaseData.senderUsername,
            owner = firebaseData.senderId == userId,
            type = firebaseData.type,
            data = firebaseData.data,
            chatId = chatId,
            messageId = firebaseData.messageId,
        )
    }

    fun mapDbModelToEntity(dbModel: ChatDataDbModel): ChatData {
        return ChatData(
            timestamp = dbModel.timestamp,
            senderUsername = dbModel.senderUsername,
            owner = dbModel.owner,
            type = ChatDataType.valueOf(dbModel.type),
            data = dbModel.data,
            messageId = dbModel.messageId,
        )
    }
}