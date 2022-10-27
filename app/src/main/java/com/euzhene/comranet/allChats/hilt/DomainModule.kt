package com.euzhene.comranet.allChats.hilt

import com.euzhene.comranet.allChats.data.AllChatsRepoImpl
import com.euzhene.comranet.allChats.domain.AllChatsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindRepo(
        impl: AllChatsRepoImpl
    ): AllChatsRepo
}