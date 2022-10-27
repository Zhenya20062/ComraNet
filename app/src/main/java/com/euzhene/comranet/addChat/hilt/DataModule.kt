package com.euzhene.comranet.addChat.hilt

import com.euzhene.comranet.addChat.data.AddChatRepoImpl
import com.euzhene.comranet.chatImageStorage
import com.euzhene.comranet.firebaseDatabase
import com.euzhene.comranet.userReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {

    @ViewModelScoped
    @Provides
    fun provideAddChatRepoImpl(): AddChatRepoImpl {

        return AddChatRepoImpl(firebaseDatabase.reference, userReference, chatImageStorage)
    }
}