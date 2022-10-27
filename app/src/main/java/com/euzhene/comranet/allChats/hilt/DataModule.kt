package com.euzhene.comranet.allChats.hilt

import com.euzhene.comranet.allChats.data.AllChatsRepoImpl
import com.euzhene.comranet.firebaseChatReference
import com.euzhene.comranet.userReference
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
class DataModule {
    @Provides
    fun provideAllChatsRepoImpl(user: FirebaseUser): AllChatsRepoImpl {
        return AllChatsRepoImpl(firebaseChatReference, userReference, user)
    }
}