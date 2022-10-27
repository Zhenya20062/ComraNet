package com.euzhene.comranet.chatRoom.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.euzhene.comranet.chatRoom.data.local.dao.ChatDataDao
import com.euzhene.comranet.chatRoom.data.local.dao.ChatRemoteKeysDao
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel

@Database(entities = [ChatDataDbModel::class, ChatRemoteKeysDbModel::class], version = 5, exportSchema = false)
abstract class ChatRoomDatabase : RoomDatabase() {
    abstract fun chatDataDao(): ChatDataDao
    abstract fun chatRemoteKeysDao(): ChatRemoteKeysDao

    companion object {
        private var INSTANCE: ChatRoomDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "chat_db"

        fun getInstance(context: Context): ChatRoomDatabase {
            INSTANCE?.let { return it }
            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val db = Room.databaseBuilder(context, ChatRoomDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}