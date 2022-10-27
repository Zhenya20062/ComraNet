package com.euzhene.comranet.chatRoom.data.remote

import android.net.Uri
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


    override suspend fun addFirebaseData(firebaseData: FirebaseSendData): Response<Boolean> {
        return when (firebaseData.type) {
            ChatDataType.IMAGE -> {
                val uri = firebaseData.data as Uri
                val uploadTask =
                    imageStorage.child(UUID.randomUUID().toString()).putFile(uri).await()
                val url = uploadTask.storage.downloadUrl.await()

                if (!uploadTask.task.isSuccessful) {
                    Response.Success(false)
                }
                val newFirebaseData = firebaseData.copy(data = url.toString())
                chatRef.child(chatId).child("messages").push().setValue(newFirebaseData).addOnCompleteListener { }
                chatRef.child(chatId).child("last_message").child("last_message").removeValue().await()
                chatRef.child(chatId).child("last_message").child("last_message").setValue(newFirebaseData)
                    .addOnCompleteListener { }
                Response.Success(true)
            }
            ChatDataType.MESSAGE -> {
                chatRef.child(chatId).child("messages").push().setValue(firebaseData).addOnCompleteListener { }
                chatRef.child(chatId).child("last_message").child("last_message").removeValue().await()
                chatRef.child(chatId).child("last_message").child("last_message").setValue(firebaseData)
                    .addOnCompleteListener { }
                Response.Success(true)
            }
        }
    }

    override fun observeFirebaseData(): Flow<FirebaseData> {
        return callbackFlow {
            chatRef.child(chatId).child("last_message").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val firebaseData = snapshot.getValue(FirebaseData::class.java)
                    trySend(firebaseData!!)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
            awaitClose()
        }
    }

}