package com.euzhene.comranet.chatRoom.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.euzhene.comranet.TAG_DATA
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseDataModel
import com.euzhene.comranet.getSenderNameFromUserQuery
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.sql.Date

@OptIn(ExperimentalPagingApi::class)
class ChatRoomRemoteMediator(
    private val chatRoomDatabase: ComranetRoomDatabase,
    private val messagesQuery: com.google.firebase.firestore.Query,
    private val senderNameFromUserQuery: com.google.firebase.firestore.Query,
    private val mapper: ChatRoomMapper,
    private val userId: String,
    private val chatId: String,
) : RemoteMediator<Int, ChatDataDbModel>() {

    private val chatDataDao = chatRoomDatabase.chatDataDao()
    private val remoteKeysDao = chatRoomDatabase.chatDataRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatDataDbModel>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.timestamp
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstTime(state)
                    val prevPage =
                        remoteKeys?.prev ?: return MediatorResult.Success(remoteKeys != null)
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastTime(state)
                    val nextPage =
                        remoteKeys?.next ?: return MediatorResult.Success(remoteKeys != null)
                    nextPage
                }

            }

            val snapshot = if (currentPage == null) messagesQuery
                .limit(state.config.initialLoadSize.toLong())
                .get().await()
            else messagesQuery.startAfter(Timestamp(Date(currentPage)))
                .limit(state.config.pageSize.toLong())
                .get().await()

            val endOfPaginationReached = snapshot.isEmpty

            if (endOfPaginationReached) {
                return MediatorResult.Success(true)
            }

            val firebaseDataList = snapshot.documents.map {
                val data = it.toObject(FirebaseDataModel::class.java) ?: throw RuntimeException(
                    "Impossible to convert this data snapshot into FirebaseData"
                )
                var senderName = ""
                if (data.sender_id != userId) {
                    senderName =
                        getSenderNameFromUserQuery(data.sender_id)!!
                }

                data.copy(message_id = it.id, senderName = senderName)
            }.toMutableList()

            val chatDataDbList = firebaseDataList.map { mapper.mapDtoToDbModel(it) }

            val nextPage = chatDataDbList.last().timestamp
            val prevPage = if (currentPage == null) null else chatDataDbList.first().timestamp
            chatRoomDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatDataDao.deleteAll(chatId)
                    remoteKeysDao.deleteAll(chatId)
                }
                val keys = chatDataDbList.map {
                    ChatRemoteKeysDbModel(
                        it.timestamp,
                        prevPage,
                        nextPage,
                        chatId
                    )
                }
                chatDataDao.insertChatDataList(chatDataDbList)
                remoteKeysDao.insertAll(keys)
            }
            MediatorResult.Success(false)
        } catch (e: Exception) {
            Log.d(TAG_DATA, "error: $e")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ChatDataDbModel>
    ): ChatRemoteKeysDbModel? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.timestamp?.let { timestamp ->
                remoteKeysDao.getRemoteKey(timestamp, chatId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstTime(
        state: PagingState<Int, ChatDataDbModel>
    ): ChatRemoteKeysDbModel? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let {
            remoteKeysDao.getRemoteKey(it.timestamp, chatId)
        }
    }

    private suspend fun getRemoteKeyForLastTime(
        state: PagingState<Int, ChatDataDbModel>
    ): ChatRemoteKeysDbModel? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let {
            remoteKeysDao.getRemoteKey(it.timestamp, chatId)
        }
    }
}