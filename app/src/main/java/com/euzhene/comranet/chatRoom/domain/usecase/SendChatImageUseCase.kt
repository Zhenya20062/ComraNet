package com.euzhene.comranet.chatRoom.domain.usecase

import android.net.Uri
import com.euzhene.comranet.util.Response
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import javax.inject.Inject

class SendChatImageUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    suspend operator fun invoke(imageUri: Uri): Response<Boolean> {
        return repository.sendChatImage(imageUri)
    }
}