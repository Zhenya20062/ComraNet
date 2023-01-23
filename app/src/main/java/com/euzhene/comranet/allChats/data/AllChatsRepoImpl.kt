package com.euzhene.comranet.allChats.data

import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.paging.PagingDataSource
import com.euzhene.comranet.allChats.domain.AllChatsRepo
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabaseFirestore
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map


class AllChatsRepoImpl(
    private val user: FirebaseUser,
    private val pagingDataSource: PagingDataSource,
    private val roomDatabase: ComranetRoomDatabase,
    private val remoteDatabase: RemoteDatabaseFirestore,
) : AllChatsRepo {
    //todo: add mapper
    override suspend fun observeNewChats() {
        remoteDatabase.observeNewChatInfo().collectLatest {
            roomDatabase.withTransaction {
                val chatInfoDbModel = ChatInfoDbModel(
                    it.chat_id, it.chat_name, it.members,
                    it.photo_url,
                )
                roomDatabase.chatInfoDao().insertChatInfo(chatInfoDbModel)
            }
        }
    }

    override fun getAllChats(): Flow<PagingData<ChatInfo>> {
        return pagingDataSource.getChatData().map {
            it.map {
                ChatInfo(
                    it.chatId,
                    it.chatName,
                    it.members,
                    it.chatPhoto
                )
            }
        }
    }

    override suspend fun getChatInfoCount(): Int {
        return roomDatabase.chatInfoDao().getChatInfoCount()
    }
}