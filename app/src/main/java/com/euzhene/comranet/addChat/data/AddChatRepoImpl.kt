package com.euzhene.comranet.addChat.data

import com.euzhene.comranet.addChat.domain.AddChatRepo
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.util.Response
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class AddChatRepoImpl(
    private val rtdRef: DatabaseReference,
    private val storageRef: StorageReference
) : AddChatRepo {
    override fun createChat(chatInfo: ChatInfo): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading(Unit))
            var chatPhoto: String? = null
            if (chatInfo.chatUri != null) {
                val uploadTask = storageRef.child(UUID.randomUUID().toString())
                    .putFile(chatInfo.chatUri).await()
                if (!uploadTask.task.isSuccessful) {
                    trySend(
                        Response.Error(
                            uploadTask.task.exception?.localizedMessage ?: "Storage error"
                        )
                    )
                }
                chatPhoto = uploadTask.storage.downloadUrl.await().toString()
            }


            val chatInfoFirebase = ChatInfoFirebase(
                chatName = chatInfo.chatName,
                members = chatInfo.members,
                chatPhoto = chatPhoto
            )
            rtdRef.child("chats").push().apply {
                child("chat_info").setValue(chatInfoFirebase)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(Response.Success(Unit))
                        } else trySend(
                            Response.Error(
                                it.exception?.localizedMessage ?: "Firebase Error"
                            )
                        )
                    }

            }
            awaitClose { }
        }
    }

    override suspend fun getAllUsers(): List<UserInfo> {
        val result = rtdRef.child("users").get().await() //todo try to call it with no network
        val usernames = result.children.mapNotNull {
            val username = it.child("username").getValue(String::class.java)!!
            val photo = it.child("photo").getValue(String::class.java)
            UserInfo(username = username, login = it.key!!, photoUrl = photo)
        }

        return usernames
    }
}