package com.euzhene.comranet.allChats.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel

@Dao
interface ChatInfoDao {
    @Query("select * from chat_info")
    fun getAllChatInfoList():PagingSource<Int, ChatInfoDbModel>

    @Insert(entity = ChatInfoDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatInfoList(chatInfoList: List<ChatInfoDbModel>)

    @Query("delete from chat_info")
    suspend fun deleteAll()
}