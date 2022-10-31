package com.euzhene.comranet.chatRoom.data.remote

import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.util.Response
import kotlinx.coroutines.flow.Flow

interface RemoteDatabase {
    suspend fun addFirebaseData(firebaseData: FirebaseSendData): Flow<Response<Unit>>
    suspend fun changeFirebaseData(firebaseData: FirebaseChangeData):Flow<Response<Unit>>
    fun observeNewFirebaseData(): Flow<FirebaseData>
    fun observeChangedFirebaseData():Flow<FirebaseData>
    var chatId: String
}