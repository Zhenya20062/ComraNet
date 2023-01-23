package com.euzhene.comranet.chatRoom.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.domain.entity.ChatData

@Dao
interface ChatDataDao {
    @Query("select * from chat_data where chatId=:chatId order by timestamp desc")
    fun getChatDataList(chatId: String): PagingSource<Int, ChatDataDbModel>

    @Query("select * from chat_data where messageId=:messageId")
    suspend fun getChatDataByMessageId(messageId:String):ChatDataDbModel?

    //@Query("select * from chat_data where chatId=:chatId order by timestamp desc limit 1")
   // suspend fun getLastItem(chatId: String): ChatDataDbModel

    @Insert(entity = ChatDataDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatDataList(chatDataList: List<ChatDataDbModel>)

    @Insert(entity = ChatDataDbModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatData(chatData: ChatDataDbModel)

    //"delete from chat_data where chatId=:chatId and messageId != (select messageId from chat_data where chatId=:chatId order by timestamp desc limit 1)"
    @Query("delete from chat_data where chatId=:chatId")
    suspend fun deleteAll(chatId: String)

    @Query("update chat_data set data=:data where messageId=:messageId")
    fun updateChatData(data:String, messageId: String)


}