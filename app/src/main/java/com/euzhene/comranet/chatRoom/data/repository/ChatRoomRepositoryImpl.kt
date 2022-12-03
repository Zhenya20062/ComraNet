package com.euzhene.comranet.chatRoom.data.repository

import android.net.Uri
import androidx.paging.PagingData
import androidx.paging.map
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSource
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabase
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.chatRoom.domain.entity.ChatData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.chatRoom.domain.entity.PollData
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.onesignal.OneSignal
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@ViewModelScoped
class ChatRoomRepositoryImpl @Inject constructor(
    private val pagingDataSource: PagingDataSource,
    private val remoteDatabase: RemoteDatabase,
    private val roomDatabase: ChatRoomDatabase,
    private val mapper: ChatRoomMapper,
    private val user: FirebaseUser,
) : ChatRoomRepository {
    private lateinit var chatId: String

    override fun getChatData(): Flow<PagingData<ChatData>> {
        return pagingDataSource.getChatData().map {
            it.map { dbModel ->
                mapper.mapDbModelToEntity(dbModel)
            }
        }

    }

    override fun observeNewChatData() {
        CoroutineScope(Dispatchers.IO).launch {
            remoteDatabase.observeNewFirebaseData().map {
                mapper.mapDtoToDbModel(it, user.uid, chatId)
            }.collectLatest {
                val chatModel = roomDatabase.chatDataDao().getChatDataByMessageId(it.messageId)
                if (chatModel == null) {
                    roomDatabase.chatDataDao().insertChatData(it)

                } else {
                    roomDatabase.chatDataDao().updateChatData(it.data, it.messageId)
                }
            }
        }
    }

    override fun observeChangedChatData() {
        CoroutineScope(Dispatchers.IO).launch {
            remoteDatabase.observeChangedFirebaseData().collectLatest {
                roomDatabase.chatDataDao().updateChatData(it.data, it.messageId)
            }
        }
    }

    override fun setChatId(id: String) {
        chatId = id
        remoteDatabase.chatId = id
        remoteDatabase.userId = user.uid
        pagingDataSource.chatId = id
    }

    override suspend fun sendChatImage(imageUri: Uri): Flow<Response<Unit>> {

        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.IMAGE,
            data = imageUri.toString(),
            user = user
        )
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }

    override suspend fun sendChatMessage(message: String): Flow<Response<Unit>> {

        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.MESSAGE,
            data = message,
            user = user
        )
        sendNotification(firebaseSendData)
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }


    override suspend fun sendChatPoll(pollData: PollData): Flow<Response<Unit>> {
        val pollJson = Gson().toJson(pollData)
        val firebaseSendData = mapper.mapEntityToDto(
            type = ChatDataType.POLL,
            data = pollJson,
            user = user
        )
        return remoteDatabase.addFirebaseData(firebaseSendData)
    }

    override suspend fun changeChatPoll(chatData: ChatData): Flow<Response<Unit>> {
        val firebaseChangeData = mapper.mapEntityToDto(chatData)
        return remoteDatabase.changeFirebaseData(firebaseChangeData)
    }

    private suspend fun sendNotification(firebaseSendData: FirebaseSendData) {
        val notificationList = remoteDatabase.getUserNotificationIdList().toMutableList().apply {
            this.remove(OneSignal.getDeviceState()!!.userId)
        }
        val body =
            "{\"include_player_ids\":[\"${notificationList.joinToString()}\"],\"contents\":{\"en\":\"${firebaseSendData.data}\"},\"name\":\"euzhene\",\"app_id\":\"de989c51-3919-4fad-969a-fcedaf46bf86\"}"

        OneSignal.postNotification(
            body,
            object : OneSignal.PostNotificationResponseHandler {
                override fun onSuccess(p0: JSONObject?) {}
                override fun onFailure(p0: JSONObject?) {}
            })
    }
}