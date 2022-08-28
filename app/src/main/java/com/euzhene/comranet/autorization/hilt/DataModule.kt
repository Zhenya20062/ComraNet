package com.euzhene.comranet.autorization.hilt

import com.euzhene.comranet.autorization.data.AuthRepoImpl
import com.euzhene.comranet.userReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class DataModule {
    @Provides
    fun provideAuthImpl(): AuthRepoImpl {
        return AuthRepoImpl(userReference)
    }
}