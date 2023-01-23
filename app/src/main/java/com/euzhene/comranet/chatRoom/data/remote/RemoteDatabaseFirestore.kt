package com.euzhene.comranet.chatRoom.data.remote

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseDataModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendDataModel
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface RemoteDatabaseFirestore {
    var chatId: String
  //  var userId: String

    //methods for chat room
    suspend fun addFirebaseData(firebaseData: FirebaseSendDataModel): Flow<Response<Unit>>
    suspend fun changeFirebaseData(firebaseData: FirebaseChangeData): Flow<Response<Unit>>
    fun observeNewFirebaseData(): Flow<FirebaseDataModel>
    fun observeChangedFirebaseData(): Flow<FirebaseDataModel>

    fun getChatInfo(): Flow<Result<ChatInfo>>

    suspend fun getUserNotificationIdList(): List<String>

    //methods for all chats
    fun observeNewChatInfo(): Flow<ChatInfo>

}