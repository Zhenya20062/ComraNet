package com.euzhene.comranet.chatRoom.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.google.firebase.database.Query
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class ChatRoomRemoteMediator(
    private val chatRoomDatabase: ChatRoomDatabase,
    private val query: Query,
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
            val snapshot = if (currentPage == null) query.get().await()
            else query.endBefore(currentPage.toDouble()).get().await()

            val endOfPaginationReached = !snapshot.hasChildren()

            val firebaseDataList = snapshot.children.map {
                it.getValue(FirebaseData::class.java) ?: throw RuntimeException(
                    "Impossible to convert this data snapshot into FirebaseData"
                )
            }.toMutableList()

            if (currentPage == null && firebaseDataList.isNotEmpty()) firebaseDataList.removeLast()

            val chatDataDbList = firebaseDataList
                .map { mapper.mapDtoToDbModel(it, userId, chatId) }

            val nextPage = if (endOfPaginationReached) null else chatDataDbList.first().timestamp
            val prevPage = if (currentPage == null) null else chatDataDbList.last().timestamp
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
            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
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