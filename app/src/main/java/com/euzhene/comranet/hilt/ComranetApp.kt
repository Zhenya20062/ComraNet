package com.euzhene.comranet.hilt

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import dagger.BindsInstance
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Singleton

@HiltAndroidApp
class ComranetApp:Application() {

}