package com.euzhene.comranet.chatRoom.hilt

import com.euzhene.comranet.chatRoom.data.repository.ChatRoomRepositoryImpl
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class DomainModule {
    @Binds
    abstract fun bindChatRoomRepo(impl:ChatRoomRepositoryImpl):ChatRoomRepository
}