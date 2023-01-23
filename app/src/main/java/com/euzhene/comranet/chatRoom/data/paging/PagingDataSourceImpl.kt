package com.euzhene.comranet.chatRoom.data.paging

import androidx.paging.*
import com.euzhene.comranet.*
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow

class PagingDataSourceImpl(
    private val mapper: ChatRoomMapper,
    private val chatRoomDatabase: ComranetRoomDatabase,
    private val user: FirebaseUser,
) : PagingDataSource {
    override var chatId = ""

    @OptIn(ExperimentalPagingApi::class)
    override fun getChatData(): Flow<PagingData<ChatDataDbModel>> {
        val pagingSourceFactory = { chatRoomDatabase.chatDataDao().getChatDataList(chatId) }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                prefetchDistance = PREFETCH_DISTANCE
            ),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = ChatRoomRemoteMediator(
                chatRoomDatabase,
                messagesFirestore()
                    .orderBy(FIRESTORE_TIMESTAMP_NAME, Query.Direction.DESCENDING)
                    .whereEqualTo(FIRESTORE_CHAT_ID_NAME, chatId),
                usersFirestore(),
                mapper,
                user.uid,
                chatId
            )
        ).flow
    }


    companion object {
        private const val PAGE_SIZE = 15
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE * 2
        private const val PREFETCH_DISTANCE = 10
    }
}