package com.euzhene.comranet.autorization.hilt

import com.euzhene.comranet.autorization.data.AuthRepoImpl
import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {
    @Binds
    abstract fun bindAuthRepo(impl: AuthRepoImpl): AuthRepo
}