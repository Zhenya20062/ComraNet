package com.euzhene.comranet.allChats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.allChats.data.local.model.ChatInfoRemoteKeysDbModel

@Dao
interface ChatInfoRemoteKeysDao {
    @Query("select * from chat_info_remote_keys where chatId=:chatId")
    suspend fun getRemoteKey(chatId: String): ChatInfoRemoteKeysDbModel

    @Insert(entity = ChatInfoRemoteKeysDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<ChatInfoRemoteKeysDbModel>)

    @Query("delete from chat_info_remote_keys")
    suspend fun deleteAll()
}