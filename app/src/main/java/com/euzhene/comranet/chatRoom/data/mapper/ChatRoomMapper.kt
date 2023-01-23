package com.euzhene.comranet.chatRoom.data.mapper

import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseDataModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendDataModel
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType

class ChatRoomMapper(
   private val userId: String,
) {
    fun mapEntityToDto(type: ChatDataType, data: String, chatId:String): FirebaseSendDataModel {
        return FirebaseSendDataModel(
            sender_id = userId,
            type = type,
            data = data,
            chat_id = chatId
        )
    }

    fun mapEntityToDto(chatData: ChatData): FirebaseChangeData {
        return FirebaseChangeData(
            messageId = chatData.messageId,
            data = chatData.data,
        )
    }

//    fun mapDtoToEntity(firebaseData: FirebaseDataModel): ChatData {
//        return ChatData(
//            messageId = firebaseData.messageId,
//            timestamp = firebaseData.timestamp,
//            senderUsername = firebaseData.senderName,
//            owner = firebaseData.senderId == userId,
//            type = ChatDataType.valueOf(firebaseData.type),
//            data = firebaseData.data,
//        )
//    }

    fun mapDtoToDbModel(firebaseData: FirebaseDataModel): ChatDataDbModel {
        return ChatDataDbModel(
            timestamp = firebaseData.timestamp.seconds*1000,
            senderUsername = firebaseData.senderName,
            owner = firebaseData.sender_id == userId,
            type = firebaseData.type,
            data = firebaseData.data,
            chatId = firebaseData.chat_id,
            messageId = firebaseData.message_id,
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