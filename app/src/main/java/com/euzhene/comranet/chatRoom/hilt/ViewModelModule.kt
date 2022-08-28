package com.euzhene.comranet.chatRoom.hilt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.assisted.AssistedFactory

class ViewModelModule {
companion object {
    @Suppress("UNCHECKED_CAST")
    fun provideFactory(
        assistedFactory: ChatRoomViewModelAssistedFactory,
        user: FirebaseUser,
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(user) as T
        }
    }
}


    @AssistedFactory
    interface ChatRoomViewModelAssistedFactory {
        fun create(firebaseUser: FirebaseUser): ChatRoomViewModel
    }
}