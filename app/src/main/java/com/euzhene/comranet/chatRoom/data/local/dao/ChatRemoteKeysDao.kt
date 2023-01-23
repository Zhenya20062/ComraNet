package com.euzhene.comranet.chatRoom.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel

@Dao
interface ChatRemoteKeysDao {
    @Query("select * from chat_remote_keys where chatId=:chatId and timestamp = :timestamp")
    suspend fun getRemoteKey(timestamp: Long, chatId: String): ChatRemoteKeysDbModel

    @Query("select * from chat_remote_keys where chatId=:chatId order by timestamp desc limit 1")
    suspend fun getLastRemoteKey(chatId: String):ChatRemoteKeysDbModel

    @Insert(entity = ChatRemoteKeysDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<ChatRemoteKeysDbModel>)

    @Insert(entity = ChatRemoteKeysDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey:ChatRemoteKeysDbModel)

    //"delete from chat_remote_keys where chatId=:chatId and timestamp != (select timestamp from chat_remote_keys where chatId=:chatId order by timestamp desc limit 1)"
    @Query("delete from chat_remote_keys where chatId=:chatId")
    suspend fun deleteAll(chatId: String)
}