package com.euzhene.comranet.chatRoom.data.remote

import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface RemoteDatabase {
    suspend fun addFirebaseData(firebaseData: FirebaseSendData): Flow<Response<Unit>>
    fun observeFirebaseData(): Flow<FirebaseData>
    var chatId: String
}