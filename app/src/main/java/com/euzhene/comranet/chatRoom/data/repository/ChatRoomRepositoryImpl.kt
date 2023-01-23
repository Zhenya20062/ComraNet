package com.euzhene.comranet.chatRoom.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.euzhene.comranet.TAG_DATA
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSource
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabaseFirestore
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendDataModel
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject

@ViewModelScoped
class ChatRoomRepositoryImpl @Inject constructor(
    private val pagingDataSource: PagingDataSource,
    private val remoteDatabase: RemoteDatabaseFirestore,
    private val roomDatabase: ComranetRoomDatabase,
    private val mapper: ChatRoomMapper,
    private val user: FirebaseUser,
) : ChatRoomRepository {
    private lateinit var chatId: String

    override fun getGroupInfo(): Flow<Result<ChatInfo>> {
        return remoteDatabase.getChatInfo()
    }

    override fun getChatData(): Flow<PagingData<ChatData>> {
        return pagingDataSource.getChatData().map {
            it.map { dbModel ->
                mapper.mapDbModelToEntity(dbModel)
            }
        }

    }

    override suspend fun observeNewChatData() {
        remoteDatabase.observeNewFirebaseData().map {
            mapper.mapDtoToDbModel(it)
        }.collectLatest {
            Log.d(TAG_DATA, "observeNewChatData: $it")
            roomDatabase.withTransaction {
                roomDatabase.chatDataDao().insertChatData(it)
                val chatModel = roomDatabase.chatDataDao().getChatDataByMessageId(it.messageId)
                if (chatModel == null) {
                    roomDatabase.chatDataDao().insertChatData(it)
                } else {
                    roomDatabase.chatDataDao().updateChatData(it.data, it.messageId)
                }
            }

        }
    }

    override suspend fun observeChangedChatData() {
        remoteDatabase.observeChangedFirebaseData().collectLatest {
            roomDatabase.chatDataDao().updateChatData(it.data, it.message_id)
        }
    }

    override fun setChatId(id: String) {
        chatId = id
        remoteDatabase.chatId = id
        //  remoteDatabase.userId = user.uid
        pagingDataSource.chatId = id
    }

    override suspend fun sendChatImage(imageUri: Uri): Flow<Response<Unit>> {

        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.IMAGE,
            data = imageUri.toString(),
            chatId = chatId,
        )
        sendNotification(firebaseSendData)
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }

    override suspend fun sendChatMessage(message: String): Flow<Response<Unit>> {

        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.MESSAGE,
            data = message,
            chatId = chatId,
        )
        sendNotification(firebaseSendData)
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }


    override suspend fun sendChatPoll(pollData: PollData): Flow<Response<Unit>> {
        val pollJson = Gson().toJson(pollData)
        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.POLL,
            data = pollJson,
            chatId = chatId,
        )
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }

    override suspend fun changeChatPoll(chatData: ChatData): Flow<Response<Unit>> {
        val firebaseChangeData = mapper.mapEntityToDto(chatData)
        return remoteDatabase.changeFirebaseData(firebaseChangeData)
    }

    private suspend fun sendNotification(firebaseSendData: FirebaseSendDataModel) {
        val notificationList = remoteDatabase.getUserNotificationIdList().toMutableList().apply {
            this.remove(OneSignal.getDeviceState()!!.userId)
        }


        val body = if (firebaseSendData.type == ChatDataType.MESSAGE)
            "{\"include_player_ids\":[\"${notificationList.joinToString("\",\"")}\"],\"contents\":{\"en\":\"${firebaseSendData.data}\"},\"name\":\"euzhene\",\"app_id\":\"de989c51-3919-4fad-969a-fcedaf46bf86\"}"
        else {
            //for image
            "{\"include_player_ids\":[\"${notificationList.joinToString("\",\"")}\"],\"contents\":{\"en\":\"Image\"},\"name\":\"euzhene\",\"app_id\":\"de989c51-3919-4fad-969a-fcedaf46bf86\",\"big_picture\":\"${firebaseSendData.data}\"}"
        }

        OneSignal.postNotification(
            body,
            object : OneSignal.PostNotificationResponseHandler {
                override fun onSuccess(p0: JSONObject?) {}
                override fun onFailure(p0: JSONObject?) {}
            })
    }
}