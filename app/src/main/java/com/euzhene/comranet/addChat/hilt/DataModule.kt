package com.euzhene.comranet.addChat.hilt

import com.euzhene.comranet.*
import com.euzhene.comranet.addChat.data.AddChatRepoImpl
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

        return AddChatRepoImpl(
            userFirestoreRef = usersFirestore(),
            chatInfoFirestoreRef = chatInfoFirestore(),
            chatMembersFirestoreRef = chatMembersFirestore(),
            storageRef = chatImageStorage
        )
    }
}