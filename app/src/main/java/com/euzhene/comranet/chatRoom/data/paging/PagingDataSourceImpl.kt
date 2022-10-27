package com.euzhene.comranet.chatRoom.data.paging

import androidx.paging.*
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatMessages
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class PagingDataSourceImpl(
    //private val queryByName: Query,
    private val mapper: ChatRoomMapper,
    private val chatRoomDatabase: ChatRoomDatabase,
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
                chatMessages(chatId),
                mapper,
                user.uid,
                chatId
            )
        ).flow
    }


    companion object {
        const val PAGE_SIZE = 10
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE
        private const val PREFETCH_DISTANCE = 3
    }
}