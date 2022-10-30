package com.euzhene.comranet.chatRoom.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.euzhene.comranet.TAG_DATA
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSource
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabase
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class ChatRoomRepositoryImpl @Inject constructor(
    private val pagingDataSource: PagingDataSource,
    private val remoteDatabase: RemoteDatabase,
    private val mapper: ChatRoomMapper,
    private val user: FirebaseUser,
) : ChatRoomRepository {

    override fun getChatData(): Flow<PagingData<ChatData>> {
        return pagingDataSource.getChatData().map {
            it.map { dbModel ->
                mapper.mapDbModelToEntity(dbModel)
            }
        }
    }

    override fun observeChatData(): Flow<ChatData> {

        return remoteDatabase.observeFirebaseData().map() {
            mapper.mapDtoToEntity(it, user.uid)
        }
    }

    override fun setChatId(id: String) {
        remoteDatabase.chatId = id
        pagingDataSource.chatId = id
    }

    override suspend fun sendChatImage(imageUri: Uri): Flow<Response<Unit>> {
        val firebaseSendData = FirebaseSendData(
            senderUsername = user.displayName!!,
            senderId = user.uid,
            type = ChatDataType.IMAGE,
            data = imageUri,
        )
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }

    override suspend fun sendChatMessage(message: String): Flow<Response<Unit>> {
        val firebaseSendData = FirebaseSendData(
            senderUsername = user.displayName!!,
            senderId = user.uid,
            type = ChatDataType.MESSAGE,
            data = message,
        )
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }
}