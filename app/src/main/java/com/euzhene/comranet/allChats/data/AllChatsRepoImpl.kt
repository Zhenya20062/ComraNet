package com.euzhene.comranet.allChats.data

import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.AllChatsRepo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class AllChatsRepoImpl(
    private val ref: DatabaseReference,
    private val userReference: DatabaseReference,
    private val user: FirebaseUser,
) : AllChatsRepo {
    override fun getAllChats(): Flow<Response<List<ChatInfoWithId>>> {
        return callbackFlow {
            trySend(Response.Loading())

            var chats: List<String> = emptyList()
            userReference.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.children.forEach {
                        val uid = it.child("uid").getValue<String>()
                        if (uid == user.uid) {
                            chats = it.child("chats").children.map { it.getValue<String>()!! }

                        }
                    }
                }
            }
            ref.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val chatInfoList = it.result!!.children.map {
                        val chatInfo = it.child("chat_info").getValue(ChatInfo::class.java)!!
                        ChatInfoWithId(chatInfo, it.key!!)
                    }

                    trySend(Response.Success(chatInfoList.filter { chats.contains(it.chatId) }))
                } else {
                    trySend(Response.Error(it.exception?.localizedMessage ?: "Firebase error"))
                }

            }

            awaitClose { }
        }
    }
}