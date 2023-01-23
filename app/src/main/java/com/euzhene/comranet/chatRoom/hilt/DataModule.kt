package com.euzhene.comranet.chatRoom.hilt

import android.content.Context
import com.euzhene.comranet.*
import com.euzhene.comranet.chatRoom.data.local.ComranetRoomDatabase
import com.euzhene.comranet.chatRoom.data.mapper.ChatRoomMapper
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSource
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSourceImpl
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabase
import com.euzhene.comranet.chatRoom.data.remote.FirebaseDatabaseImpl
import com.euzhene.comranet.chatRoom.data.remote.FirestoreDatabaseImpl
import com.euzhene.comranet.chatRoom.data.remote.RemoteDatabaseFirestore
import com.google.firebase.auth.FirebaseUser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindRemoteDatabase(impl: FirebaseDatabaseImpl): RemoteDatabase

    @Binds
    abstract fun bindRemoteDatabaseFirestore(impl: FirestoreDatabaseImpl): RemoteDatabaseFirestore


    @Binds
    abstract fun bindPagingDataSource(impl: PagingDataSourceImpl): PagingDataSource

    companion object {
        @Provides
        @ViewModelScoped
        fun provideFirestoreImpl(firebaseUser: FirebaseUser): FirestoreDatabaseImpl {
            return FirestoreDatabaseImpl(
                messagesFirestoreRef = messagesFirestore(),
                usersFirestoreRef = usersFirestore(),
                chatInfoFirestoreRef = chatInfoFirestore(),
                chatMemberFirestoreRef = chatMembersFirestore(),
                userId = firebaseUser.uid,
            )
        }

        @Provides
        @ViewModelScoped
        fun provideRemoteDatabaseImpl(): FirebaseDatabaseImpl {
            return FirebaseDatabaseImpl(firebaseChatReference, userReference)
        }

        @Provides
        @ViewModelScoped
        fun providePagingDataSourceImpl(
            mapper: ChatRoomMapper,
            chatRoomDatabase: ComranetRoomDatabase,
            user: FirebaseUser,
        ): PagingDataSourceImpl {
            return PagingDataSourceImpl(
                mapper, chatRoomDatabase, user
            )
        }

        @Provides
        fun provideChatRoomMapper(firebaseUser: FirebaseUser): ChatRoomMapper {
            return ChatRoomMapper(userId = firebaseUser.uid)
        }

        @Provides
        fun provideChatRoomDB(@ApplicationContext context: Context): ComranetRoomDatabase {
            return ComranetRoomDatabase.getInstance(context)
        }


    }

}