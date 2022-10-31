package com.euzhene.comranet.chatRoom.data.remote

import android.net.Uri
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
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
import java.util.*

class RemoteDatabaseImpl(
    private val chatRef: DatabaseReference,
) : RemoteDatabase {
    override var chatId: String = ""

    override suspend fun addFirebaseData(firebaseData: FirebaseSendData): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading())
            when (firebaseData.type) {
                ChatDataType.IMAGE -> {
                    val uri = firebaseData.data as Uri
                    imageStorage.child(UUID.randomUUID().toString()).putFile(uri)
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
                            val lastData = it.result!!.getValue<FirebaseData>()!!
                            if (lastData.messageId == firebaseData.messageId) {
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

    private fun updateLastMessage(firebaseData: Any, onComplete: () -> Unit, onError: () -> Unit) {
        chatRef.child(chatId).child("messages").push().setValue(firebaseData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    chatRef.child(chatId).child("last_message")
                        .child("last_message").apply {
                            removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    setValue(firebaseData).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            onComplete()
                                        } else {
                                            onError()
                                        }
                                    }
                                } else {
                                    onError()
                                }

                            }
                        }
                } else {
                    onError()
                }

            }
    }

    override fun observeNewFirebaseData(): Flow<FirebaseData> {
        return callbackFlow {
            chatRef.child(chatId).child("last_message")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val firebaseData = snapshot.getValue(FirebaseData::class.java)

                        trySend(firebaseData!!)
                    }
                })
            awaitClose()
        }
    }

    override fun observeChangedFirebaseData(): Flow<FirebaseData> {
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
                        val firebaseData = snapshot.getValue(FirebaseData::class.java)
                        trySend(firebaseData!!.copy(messageId = snapshot.key.toString()))
                    }

                })
            awaitClose()
        }
    }

}