package com.euzhene.comranet.chatRoom.hilt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.euzhene.comranet.chatRoom.presentation.ChatRoomViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.assisted.AssistedFactory

class ViewModelModule {
//companion object {
//    @Suppress("UNCHECKED_CAST")
//    fun provideFactory(
//        assistedFactory: ChatRoomViewModelAssistedFactory,
//        chatId:String
//    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return assistedFactory.create(
//                chatId
//            ) as T
//        }
//    }
//}
//
//
//    @AssistedFactory
//    interface ChatRoomViewModelAssistedFactory {
//        fun create(
//            chatId: String
//        ): ChatRoomViewModel
//    }
}