package com.euzhene.comranet.allChats.data

import androidx.paging.PagingData
import androidx.paging.map
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.data.paging.PagingDataSource
import com.euzhene.comranet.allChats.domain.AllChatsRepo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.getMyUserRef
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map


class AllChatsRepoImpl(
    private val ref: DatabaseReference,
    private val userReference: DatabaseReference,
    private val user: FirebaseUser,
    private val pagingDataSource: PagingDataSource,
) : AllChatsRepo {

    override fun observeChats(): Flow<ChatInfoWithId> {
        return callbackFlow {
            getMyUserRef(user.uid).child("chats").addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        val chat = snapshot.getValue<ChatInfo>()!!
                        val chatWithId = ChatInfoWithId(chatId = snapshot.key!!, chatInfo = chat)
                        trySend(chatWithId)
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                }
            )
            awaitClose()
        }

    }

    override fun getAllChats(): Flow<PagingData<ChatInfoWithId>> {
        return pagingDataSource.getChatData().map {
            it.map {
                ChatInfoWithId(
                    chatId = it.chatId,
                    chatInfo = ChatInfo(
                        chatName = it.chatName,
                        chatPhoto = it.chatPhoto,
                        members = it.members,
                    )

                )
            }
        }
        //    return callbackFlow {
        //      trySend(Response.Loading())
        //    pagingDataSource.getChatData()

        //      var chats: List<String> = emptyList()
        //          userReference.get().addOnCompleteListener {
//                if (it.isSuccessful) {
//                    it.result!!.children.forEach {
//                        val uid = it.child("uid").getValue<String>()
//                        if (uid == user.uid) {
//                            chats = it.child("chats").children.map { it.getValue<String>()!! }
//
//                        }
//                    }
//                }
//            }
//            ref.get().addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val chatInfoList = it.result!!.children.map {
//                        val chatInfo = it.child("chat_info").getValue(ChatInfo::class.java)!!
//                        ChatInfoWithId(chatInfo, it.key!!)
//                    }
//
//                    trySend(Response.Success(chatInfoList.filter { chats.contains(it.chatId) }))
//                } else {
//                    trySend(Response.Error(it.exception?.localizedMessage ?: "Firebase error"))
//                }
//
//            }

        //        awaitClose { }
        //   }
    }
}