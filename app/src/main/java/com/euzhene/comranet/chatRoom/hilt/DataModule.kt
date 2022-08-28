package com.euzhene.comranet.chatRoom.hilt

import android.content.Context
import com.euzhene.comranet.chatRoom.data.local.ChatRoomDatabase
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSource
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSourceImpl
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabase
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabaseImpl
import com.euzhene.comranet.firebaseChatReference
import com.euzhene.comranet.firebaseLastFirebaseDataReference
import com.euzhene.comranet.queryByName
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindRemoteDatabase(impl: RemoteDatabaseImpl): RemoteDatabase

    @Binds
    abstract fun bindPagingDataSource(impl: PagingDataSourceImpl): PagingDataSource

    companion object {
        @Provides
        @ActivityScoped
        fun provideRemoteDatabaseImpl(): RemoteDatabaseImpl {
            return RemoteDatabaseImpl(firebaseChatReference, firebaseLastFirebaseDataReference)
        }

        @Provides
        @ActivityScoped
        fun providePagingDataSourceImpl(
            mapper: ChatRoomMapper,
            chatRoomDatabase: ChatRoomDatabase
        ): PagingDataSourceImpl {
            return PagingDataSourceImpl(
                queryByName, mapper, chatRoomDatabase
            )
        }


        @Provides
        fun provideChatRoomDB(@ApplicationContext context: Context): ChatRoomDatabase {
            return ChatRoomDatabase.getInstance(context)
        }



    }

}