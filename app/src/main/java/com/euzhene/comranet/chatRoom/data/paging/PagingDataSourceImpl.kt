package com.euzhene.comranet.chatRoom.data.paging

import androidx.paging.*
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.google.firebase.database.Query
import kotlinx.coroutines.flow.Flow

class PagingDataSourceImpl(
    private val queryByName: Query,
    private val mapper: ChatRoomMapper,
    private val chatRoomDatabase: ChatRoomDatabase,
) : PagingDataSource {
    private lateinit var userId:String
    @OptIn(ExperimentalPagingApi::class)
    override fun getChatData(): Flow<PagingData<ChatDataDbModel>> {
        val pagingSourceFactory = { chatRoomDatabase.chatDataDao().getChatDataList() }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                prefetchDistance = PREFETCH_DISTANCE
            ),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = ChatRoomRemoteMediator(
                chatRoomDatabase,
                queryByName,
                mapper,
                userId,
            )
        ).flow
}

    override fun setUserId(id: String) {
        userId = id
    }

    companion object {
    const val PAGE_SIZE = 10
    private const val INITIAL_LOAD_SIZE = PAGE_SIZE
    private const val PREFETCH_DISTANCE = 3
}
}