package com.euzhene.comranet.addChat.hilt

import com.euzhene.comranet.addChat.data.AddChatRepoImpl
import com.euzhene.comranet.addChat.domain.AddChatRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {
    @Binds
    abstract fun bindAddChatRepo(impl: AddChatRepoImpl): AddChatRepo
}