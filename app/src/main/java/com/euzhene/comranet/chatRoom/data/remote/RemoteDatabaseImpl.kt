package com.euzhene.comranet.chatRoom.data.remote

import android.net.Uri
import android.util.Log
import com.euzhene.comranet.TAG_DATA
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendData
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.imageStorage
import com.euzhene.comranet.util.Response
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
//                                        chatRef.child(chatId).child("messages").push()
//                                            .setValue(newFirebaseData).addOnCompleteListener { }

                                        updateLastMessage(newFirebaseData, onComplete = {
                                            trySend(Response.Success(Unit))
                                        }, onError = {
                                            trySend(Response.Error("Image wasn't delivered"))
                                        })
//                                        chatRef.child(chatId).child("last_message")
//                                            .child("last_message").removeValue()
//                                            .addOnCompleteListener { }
//                                        chatRef.child(chatId).child("last_message")
//                                            .child("last_message").setValue(newFirebaseData)
//                                            .addOnCompleteListener { }
                                    } else {
                                        trySend(Response.Error("Image wasn't delivered"))
                                    }
                                }
                            } else {
                                trySend(Response.Error("Image wasn't delivered"))
                            }
                        }

                }
                ChatDataType.MESSAGE -> {
//                    chatRef.child(chatId).child("messages").push().setValue(firebaseData)
//                        .addOnCompleteListener { }
                    updateLastMessage(firebaseData, onComplete = {
                        trySend(Response.Success(Unit))
                    }, onError = {
                        trySend(Response.Error("Message wasn't delivered"))
                    })
//                    chatRef.child(chatId).child("last_message").child("last_message").removeValue()
//                        .await()
//                    chatRef.child(chatId).child("last_message").child("last_message")
//                        .setValue(firebaseData)
//                        .addOnCompleteListener { }
                    //   Response.Success(true)
                }
            }

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

    override fun observeFirebaseData(): Flow<FirebaseData> {
        return callbackFlow {
            chatRef.child(chatId).child("last_message")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val firebaseData = snapshot.getValue(FirebaseData::class.java)
                        trySend(firebaseData!!)
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
            awaitClose()
        }
    }

}