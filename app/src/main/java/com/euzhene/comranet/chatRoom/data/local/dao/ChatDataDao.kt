package com.euzhene.comranet.chatRoom.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel

@Dao
interface ChatDataDao {
    @Query("select * from chat_data order by timestamp desc")
    fun getChatDataList(): PagingSource<Int, ChatDataDbModel>

    @Query("select * from chat_data order by timestamp asc limit 1")
    suspend fun getLastItem():ChatDataDbModel

    @Insert(entity = ChatDataDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatDataList(chatDataList: List<ChatDataDbModel>)

    @Query("delete from chat_data")
    suspend fun deleteAll()
}