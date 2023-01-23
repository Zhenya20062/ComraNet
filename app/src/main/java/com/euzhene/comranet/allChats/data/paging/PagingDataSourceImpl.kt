package com.euzhene.comranet.allChats.data.paging

import androidx.paging.*
import com.euzhene.comranet.*
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.mapper.AllChatsMapper
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class PagingDataSourceImpl(
    private val mapper: AllChatsMapper,
    private val roomDatabase: ComranetRoomDatabase,
    private val user: FirebaseUser,
) : PagingDataSource {

    @OptIn(ExperimentalPagingApi::class)
    override fun getChatData(): Flow<PagingData<ChatInfoDbModel>> {
        val pagingSourceFactory = { roomDatabase.chatInfoDao().getAllChatInfoList() }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
                prefetchDistance = PREFETCH_DISTANCE
            ),
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = AllChatsRemoteMediator(
                roomDatabase,
                chatInfoFirestore(),
                chatMembersFirestore()
                    .orderBy(FIRESTORE_CHAT_ID_NAME)
                    .whereEqualTo(FIRESTORE_USER_ID_NAME, user.uid),
                mapper,
            )
        ).flow
    }


    companion object {
        const val PAGE_SIZE = 15
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE * 2
        private const val PREFETCH_DISTANCE = 5
    }
}