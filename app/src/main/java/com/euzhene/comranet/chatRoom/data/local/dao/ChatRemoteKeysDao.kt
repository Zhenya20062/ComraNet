package com.euzhene.comranet.chatRoom.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel

@Dao
interface ChatRemoteKeysDao {
    @Query("select * from chat_remote_keys where timestamp = :timestamp")
    suspend fun getRemoteKey(timestamp: Long): ChatRemoteKeysDbModel

    @Insert(entity = ChatRemoteKeysDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<ChatRemoteKeysDbModel>)

    @Query("delete from chat_remote_keys")
    suspend fun deleteAll()
}