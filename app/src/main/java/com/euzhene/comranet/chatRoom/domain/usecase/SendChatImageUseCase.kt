package com.euzhene.comranet.chatRoom.domain.usecase

import android.net.Uri
import com.euzhene.comranet.util.Response
import com.euzhene.comranet.chatRoom.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendChatImageUseCase @Inject constructor(private val repository: ChatRoomRepository) {
    suspend operator fun invoke(imageUri: Uri): Flow<Response<Unit>> {
        return repository.sendChatImage(imageUri)
    }
}