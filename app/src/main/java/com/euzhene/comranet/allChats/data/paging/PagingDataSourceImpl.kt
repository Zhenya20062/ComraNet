package com.euzhene.comranet.allChats.data.paging

import androidx.paging.*
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.mapper.AllChatsMapper
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatMessages
import com.euzhene.comranet.firebaseChatReference
import com.euzhene.comranet.userReference
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class PagingDataSourceImpl(
    private val mapper: AllChatsMapper,
    private val roomDatabase: ChatRoomDatabase,
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
                userReference.child(user.uid).child("chats"),
                mapper,
                user.uid,
            )
        ).flow
    }


    companion object {
        const val PAGE_SIZE = 20
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE
        private const val PREFETCH_DISTANCE = 3
    }
}