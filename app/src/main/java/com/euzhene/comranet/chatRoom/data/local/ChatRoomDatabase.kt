package com.euzhene.comranet.chatRoom.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.euzhene.comranet.allChats.data.local.converter.MemberListConverter
import com.euzhene.comranet.allChats.data.local.dao.ChatInfoDao
import com.euzhene.comranet.allChats.data.local.dao.ChatInfoRemoteKeysDao
import com.euzhene.comranet.allChats.data.local.model.ChatInfoDbModel
import com.euzhene.comranet.allChats.data.local.model.ChatInfoRemoteKeysDbModel
import com.euzhene.comranet.chatRoom.data.local.dao.ChatDataDao
import com.euzhene.comranet.chatRoom.data.local.dao.ChatRemoteKeysDao
import com.euzhene.comranet.chatRoom.data.local.model.ChatDataDbModel
import com.euzhene.comranet.chatRoom.data.local.model.ChatRemoteKeysDbModel

@Database(
    entities = [ChatDataDbModel::class, ChatRemoteKeysDbModel::class, ChatInfoDbModel::class, ChatInfoRemoteKeysDbModel::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(MemberListConverter::class)
abstract class ChatRoomDatabase : RoomDatabase() {
    abstract fun chatDataDao(): ChatDataDao
    abstract fun chatDataRemoteKeysDao(): ChatRemoteKeysDao
    abstract fun chatInfoDao(): ChatInfoDao
    abstract fun chatInfoRemoteKeysDao():ChatInfoRemoteKeysDao

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
                    .addTypeConverter(MemberListConverter())
                    .build()
                INSTANCE = db
                return db
            }
        }
    }
}