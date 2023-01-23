package com.euzhene.comranet.allChats.hilt

import com.euzhene.comranet.allChats.data.AllChatsRepoImpl
import com.euzhene.comranet.allChats.data.mapper.AllChatsMapper
import com.euzhene.comranet.allChats.data.paging.PagingDataSource
import com.euzhene.comranet.allChats.data.paging.PagingDataSourceImpl
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabase
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabaseFirestore
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
class DataModule {
    @Provides
    fun provideAllChatsRepoImpl(
        user: FirebaseUser,
        pagingDataSource: PagingDataSource,
        roomDatabase: ComranetRoomDatabase,
        remoteDatabase: RemoteDatabaseFirestore,
    ): AllChatsRepoImpl {
        return AllChatsRepoImpl(user, pagingDataSource, roomDatabase,remoteDatabase)
    }

    @ViewModelScoped
    @Provides
    fun providePagingDataSource(impl: PagingDataSourceImpl): PagingDataSource {
        return impl
    }

    @ViewModelScoped
    @Provides
    fun providePagingDataSourceImpl(
        mapper: AllChatsMapper,
        roomDatabase: ComranetRoomDatabase,
        user: FirebaseUser,
    ): PagingDataSourceImpl {
        return PagingDataSourceImpl(
            mapper = mapper,
            roomDatabase = roomDatabase,
            user = user,
        )
    }
}