package com.euzhene.comranet.allChats.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.euzhene.comranet.FIRESTORE_CHAT_ID_NAME
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.local.model.ChatInfoRemoteKeysDbModel
import com.euzhene.comranet.allChats.data.mapper.AllChatsMapper
import com.euzhene.comranet.allChats.data.model.ChatInfoFirebase
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.getMemberNameListFromUserQuery
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class AllChatsRemoteMediator(
    private val chatRoomDatabase: ComranetRoomDatabase,
    private val chatInfoQuery: Query,
    private val chatMembersQuery: Query,
    private val mapper: AllChatsMapper,
) : RemoteMediator<Int, ChatInfoDbModel>() {
    private val chatInfoDao = chatRoomDatabase.chatInfoDao()
    private val remoteKeysDao = chatRoomDatabase.chatInfoRemoteKeysDao()

    // TODO: write logic for ordering chat_info by last_message value
    // TODO: fix bug when there's no new data but you have old , so old data, which doesn't exist, remains
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
            val snapshot = if (currentPage == null) chatMembersQuery
                .limit(state.config.initialLoadSize.toLong())
                .get().await()
            else chatMembersQuery
                .startAfter(currentPage)
                .limit(state.config.pageSize.toLong())
                .get().await()

            val endOfPaginationReached = snapshot.isEmpty

            if (endOfPaginationReached) {
                return MediatorResult.Success(true)
            }
            val firebaseDataList = snapshot.documents.map {
                val chatId = it.get(FIRESTORE_CHAT_ID_NAME).toString()
                val chatInfoSnapshot = chatInfoQuery
                    .whereEqualTo(FieldPath.documentId(), chatId)
                    .get().await()
                val chatInfo =
                    chatInfoSnapshot.documents.first().toObject(ChatInfoFirebase::class.java)
                        ?: throw RuntimeException(
                            "Impossible to convert this data snapshot into ChatInfoFirebase"
                        )
                val members = getMemberNameListFromUserQuery(chatId)

                chatInfo.copy(chat_id = chatId, members = members)
            }

            val chatDataDbList = firebaseDataList.map { mapper.mapDtoToDbModel(it) }

            val nextPage = chatDataDbList.last().chatId
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
            MediatorResult.Success(false)
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