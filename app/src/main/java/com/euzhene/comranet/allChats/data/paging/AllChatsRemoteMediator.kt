package com.euzhene.comranet.allChats.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.local.model.ChatInfoRemoteKeysDbModel
import com.euzhene.comranet.allChats.data.mapper.AllChatsMapper
import com.euzhene.comranet.allChats.data.model.ChatInfoFirebase
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.google.firebase.database.Query
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class AllChatsRemoteMediator(
    private val chatRoomDatabase: ChatRoomDatabase,
    private val query: Query,
    private val mapper: AllChatsMapper,
    private val userId: String,
) : RemoteMediator<Int, ChatInfoDbModel>() {
    private val chatInfoDao = chatRoomDatabase.chatInfoDao()
    private val remoteKeysDao = chatRoomDatabase.chatInfoRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatInfoDbModel>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.chatId
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
            val snapshot = if (currentPage == null) query.limitToFirst(20).get().await()
            else query.orderByKey().startAfter(currentPage).limitToFirst(20).get().await()

            val endOfPaginationReached = !snapshot.hasChildren()

            val firebaseDataList = snapshot.children.map {
                val chatInfoWithoutChatId =
                    it.getValue(ChatInfoFirebase::class.java) ?: throw RuntimeException(
                        "Impossible to convert this data snapshot into ChatInfoFirebase"
                    )
                chatInfoWithoutChatId.copy(chatId = it.key!!)
            }.toMutableList()

            // if (currentPage == null && firebaseDataList.isNotEmpty()) firebaseDataList.removeLast()

            val chatDataDbList = firebaseDataList
                .map { mapper.mapDtoToDbModel(it) }
                .toMutableList()
            val nextPage = if (endOfPaginationReached) null else chatDataDbList.last().chatId
            val prevPage = if (currentPage == null) null else chatDataDbList.first().chatId

            chatRoomDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatInfoDao.deleteAll()
                    remoteKeysDao.deleteAll()
                }
                val keys = chatDataDbList.map {
                    ChatInfoRemoteKeysDbModel(
                        it.chatId,
                        prevPage,
                        nextPage,
                    )
                }
                chatInfoDao.insertChatInfoList(chatDataDbList)
                remoteKeysDao.insertAll(keys)
            }
            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ChatInfoDbModel>
    ): ChatInfoRemoteKeysDbModel? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.chatId?.let { chatId ->
                remoteKeysDao.getRemoteKey(chatId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstTime(
        state: PagingState<Int, ChatInfoDbModel>
    ): ChatInfoRemoteKeysDbModel? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let {
            remoteKeysDao.getRemoteKey(it.chatId)
        }
    }

    private suspend fun getRemoteKeyForLastTime(
        state: PagingState<Int, ChatInfoDbModel>
    ): ChatInfoRemoteKeysDbModel? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let {
            remoteKeysDao.getRemoteKey(it.chatId)
        }
    }
}