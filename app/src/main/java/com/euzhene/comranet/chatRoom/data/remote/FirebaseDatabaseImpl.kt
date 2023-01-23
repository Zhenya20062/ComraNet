package com.euzhene.comranet.chatRoom.data.remote

import android.net.Uri
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.domain.ChatInfoWithId
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseDataModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendDataModel
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.getMyUserRef
import com.euzhene.comranet.getUserGroup
import com.euzhene.comranet.imageStorage
import com.euzhene.comranet.util.Response
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseDatabaseImpl(
    private val chatRef: DatabaseReference,
    private val userRef: DatabaseReference,
) : RemoteDatabase {
    override var chatId: String = ""
    override var userId: String = ""

    override suspend fun addFirebaseData(firebaseData: FirebaseSendDataModel): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading())
            when (firebaseData.type) {
                ChatDataType.IMAGE -> {
                    imageStorage.child(UUID.randomUUID().toString())
                        .putFile(Uri.parse(firebaseData.data.toString()))
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                it.result!!.storage.downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        val newFirebaseData =
                                            firebaseData.copy(data = it.result.toString())

                                        updateLastMessage(newFirebaseData, onComplete = {
                                            trySend(Response.Success(Unit))
                                        }, onError = {
                                            trySend(Response.Error("Image wasn't sent"))
                                        })
                                    } else {
                                        trySend(Response.Error("Image wasn't sent"))
                                    }
                                }
                            } else {
                                trySend(Response.Error("Image wasn't sent"))
                            }
                        }

                }
                ChatDataType.MESSAGE -> {
                    updateLastMessage(firebaseData, onComplete = {
                        trySend(Response.Success(Unit))
                    }, onError = {
                        trySend(Response.Error("Message wasn't sent"))
                    })
                }
                ChatDataType.POLL -> {
                    updateLastMessage(firebaseData, onComplete = {
                        trySend(Response.Success(Unit))
                    }, onError = {
                        trySend(Response.Error("Poll wasn't sent"))
                    })
                }

            }

            awaitClose()
        }

    }

    private fun getLastData(
        firebaseData: FirebaseChangeData,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        chatRef.child(chatId).child("messages").child(firebaseData.messageId).child("data")
            .apply {
                chatRef.child(chatId).child("messages").limitToLast(1).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val lastData = it.result!!.children.first().getValue<FirebaseDataModel>()!!
                                .copy(message_id = it.result!!.children.first().key.toString())
                            if (lastData.message_id == firebaseData.messageId) {
                                changeLastMessage(firebaseData, onError, onComplete)
                            }
                        } else {
                            onError()
                        }
                    }
            }
    }

    private fun changeData(
        firebaseData: FirebaseChangeData,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        chatRef.child(chatId).child("messages").child(firebaseData.messageId).child("data")
            .setValue(firebaseData.data).addOnCompleteListener {
                if (it.isSuccessful) {
                    getLastData(firebaseData, onError, onComplete)
                } else {
                    onError()
                }
            }
    }

    private fun changeLastMessage(
        firebaseData: FirebaseChangeData,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        chatRef.child(chatId).child("last_message")
            .child("last_message")
            .child("data")
            .setValue(
                firebaseData.data
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete()
                } else {
                    onError()
                }
            }
    }

    override suspend fun changeFirebaseData(firebaseData: FirebaseChangeData): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading(Unit))
            changeData(
                firebaseData,
                onError = { trySend(Response.Error("Data wasn't changed")) },
                onComplete = {
                    trySend(Response.Success(Unit))
                })
            awaitClose()
        }
    }

    private fun updateLastMessage(
        firebaseData: FirebaseSendDataModel,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        chatRef.child(chatId).child("messages").push()
            .apply {
                setValue(firebaseData)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val messageId = this.key.toString()
                            chatRef.child(chatId).child("last_message")
                                .child("last_message").apply {
                                    removeValue().addOnCompleteListener {
//                                        if (it.isSuccessful) {
//                                            setValue(firebaseData.copy(messageId = messageId)).addOnCompleteListener {
//                                                if (it.isSuccessful) {
//                                                    onComplete()
//                                                } else {
//                                                    onError()
//                                                }
//                                            }
//                                        } else {
//                                            onError()
//                                        }

                                    }
                                }
                        } else {
                            onError()
                        }

                    }
            }

    }

    var isFirstData= true
    override fun observeNewFirebaseData(): Flow<FirebaseDataModel> {
        return callbackFlow {
            chatRef.child(chatId).child("last_message")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (isFirstData) {
                            isFirstData = false
                            return
                        }
                        val firebaseData = snapshot.getValue(FirebaseDataModel::class.java)

                        trySend(firebaseData!!)
                    }
                })
            awaitClose()
        }
    }

    override fun observeChangedFirebaseData(): Flow<FirebaseDataModel> {
        return callbackFlow {
            chatRef.child(chatId).child("messages")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        val firebaseData = snapshot.getValue(FirebaseDataModel::class.java)
                        trySend(firebaseData!!.copy(message_id = snapshot.key.toString()))
                    }

                })
            awaitClose()
        }
    }

    override fun getGroupInfo(): Flow<Result<ChatInfo>> {
        return callbackFlow {
            getUserGroup(userId, chatId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val chatInfo = it.result!!.getValue<ChatInfo>()!!
                    trySend(Result.success(chatInfo))
                } else {
                    trySend(Result.failure(it.exception!!))
                }
            }
            awaitClose()
        }
    }

    override suspend fun getUserNotificationIdList(): List<String> {
        val chatUsers = userRef.child(userId).child("chats").child(chatId)
            .child("members").get().await()
        val users = chatUsers.children.map {
            it.getValue<String>()!!
        }
        val notificationList = mutableListOf<String>()
        userRef.get().await().children.forEach {
            if (users.contains(it.key)) {
                val notification = it.child("notification_id").value
                if (notification != null) {
                    notificationList.add(notification.toString())
                }
            }
        }
        return notificationList
    }

    override fun observeNewChat(): Flow<ChatInfoWithId> {
        return callbackFlow {
            getMyUserRef(userId).child("chats").addChildEventListener(
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
                        // TODO: handle when chats are changed
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        // TODO: handle when chat is deleted
                    }
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                }
            )
            awaitClose()
        }
    }

}