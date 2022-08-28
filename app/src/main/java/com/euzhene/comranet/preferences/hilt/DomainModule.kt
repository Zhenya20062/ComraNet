package com.euzhene.comranet.preferences.hilt

import com.euzhene.comranet.preferences.data.PreferenceRepoImpl
import com.euzhene.comranet.preferences.domain.repository.PreferenceRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ActivityComponent::class, ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindPreferenceRepo(impl: PreferenceRepoImpl): PreferenceRepo
}