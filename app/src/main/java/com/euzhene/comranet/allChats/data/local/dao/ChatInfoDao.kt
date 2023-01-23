package com.euzhene.comranet.allChats.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel

@Dao
interface ChatInfoDao {
    @Query("select * from chat_info")
    fun getAllChatInfoList():PagingSource<Int, ChatInfoDbModel>

    @Insert(entity = ChatInfoDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatInfoList(chatInfoList: List<ChatInfoDbModel>)

    @Insert(entity = ChatInfoDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatInfo(chatInfo: ChatInfoDbModel)

    @Query("delete from chat_info")
    suspend fun deleteAll()

    @Query("select count(chatId) from chat_info")
    suspend fun getChatInfoCount():Int
}