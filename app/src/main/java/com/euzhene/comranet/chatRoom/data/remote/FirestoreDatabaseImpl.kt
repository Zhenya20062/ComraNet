package com.euzhene.comranet.chatRoom.data.remote

import android.net.Uri
import com.euzhene.comranet.*
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.allChats.data.model.ChatInfoFirebase
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseChangeData
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseDataModel
import com.euzhene.comranet.chatRoom.data.remote.dto.FirebaseSendDataModel
import com.euzhene.comranet.chatRoom.domain.entity.ChatDataType
import com.euzhene.comranet.util.Response
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange.Type
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreDatabaseImpl(
    private val messagesFirestoreRef: CollectionReference,
    private val usersFirestoreRef: CollectionReference,
    private val chatInfoFirestoreRef: CollectionReference,
    private val chatMemberFirestoreRef: CollectionReference,
    private val userId: String,
) : RemoteDatabaseFirestore {
    override lateinit var chatId: String
    //   override lateinit var userId: String

    override suspend fun addFirebaseData(firebaseData: FirebaseSendDataModel): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading())
            when (firebaseData.type) {
                ChatDataType.MESSAGE -> {
                    messagesFirestoreRef
                        .document(UUID.randomUUID().toString())
                        .set(firebaseData).addOnCompleteListener {
                            if (it.isSuccessful) {
                                trySend(Response.Success(Unit))
                            } else {
                                trySend(Response.Error(it.exception!!.localizedMessage!!))
                            }
                        }
                }
                ChatDataType.IMAGE -> {
                    imageStorage.child(UUID.randomUUID().toString())
                        .putFile(Uri.parse(firebaseData.data.toString()))
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                it.result!!.storage.downloadUrl.addOnCompleteListener {
                                    if (!it.isSuccessful) {
                                        trySend(Response.Error("Image wasn't sent"))
                                    } else {
                                        val newFirebaseData =
                                            firebaseData.copy(data = it.result.toString())
                                        messagesFirestoreRef
                                            .document(UUID.randomUUID().toString())
                                            .set(newFirebaseData).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    trySend(Response.Success(Unit))
                                                } else {
                                                    trySend(Response.Error(it.exception!!.localizedMessage!!))
                                                }
                                            }
                                    }
                                }

                            }

                        }
                }
                else -> Unit
            }
            awaitClose()
        }
    }

    override suspend fun changeFirebaseData(firebaseData: FirebaseChangeData): Flow<Response<Unit>> {
        TODO("Not yet implemented")
    }

    override fun observeNewFirebaseData(): Flow<FirebaseDataModel> {
        return callbackFlow {
            messagesFirestoreRef
                .orderBy(FIRESTORE_TIMESTAMP_NAME, Query.Direction.DESCENDING)
                .whereEqualTo(FIRESTORE_CHAT_ID_NAME, chatId)
                .limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        throw RuntimeException("exception was found in observeNewFirebaseData: ${e.message}")
                    }
                    snapshot!!.documentChanges.forEach {
                        when (it.type) {
                            Type.ADDED -> {
                                val firebaseData =
                                    it.document.toObject(FirebaseDataModel::class.java)
                                val timestamp =
                                    if (firebaseData.timestamp == null) Timestamp(
                                        it.document.getDate(
                                            FIRESTORE_TIMESTAMP_NAME,
                                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                                        )!!
                                    ) else firebaseData.timestamp
                                launch {
                                    val senderName =
                                        if (firebaseData.sender_id != userId) getSenderNameFromUserQuery(
                                            firebaseData.sender_id
                                        ) else ""
                                    trySend(
                                        firebaseData.copy(
                                            message_id = it.document.id,
                                            timestamp = timestamp,
                                            senderName = senderName
                                        )
                                    )
                                }
                            }
                            Type.MODIFIED -> {
                                // TODO: add logic for changed data
                            }
                            Type.REMOVED -> {
                                // TODO: add logic when removing data
                            }
                        }
                    }
                }
            awaitClose()
        }
    }

    override fun observeChangedFirebaseData(): Flow<FirebaseDataModel> {
        TODO("Not yet implemented")
    }

    override fun getChatInfo(): Flow<Result<ChatInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserNotificationIdList(): List<String> {
        val snapshot = chatMemberFirestoreRef
            .whereEqualTo(FIRESTORE_CHAT_ID_NAME, chatId)
            .get().await()

        val userIdList = snapshot.documents.mapNotNull {
            val userId = it.get(FIRESTORE_USER_ID_NAME, String::class.java)
            userId
        }
        val userSnapshot = usersFirestoreRef
            .whereIn(FieldPath.documentId(), userIdList)
            .get().await()

        return userSnapshot.documents.mapNotNull {
            it.get(FIRESTORE_NOTIFICATION_ID_NAME, String::class.java)
        }
    }

    override fun observeNewChatInfo(): Flow<ChatInfo> {
        return callbackFlow {
            chatMemberFirestoreRef
                .whereEqualTo(FIRESTORE_USER_ID_NAME, userId)
                .limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        throw RuntimeException("exception was found in observeNewChatInfo: ${e.message}")
                    }
                    snapshot!!.documentChanges.forEach {
                        when (it.type) {
                            Type.ADDED -> {
                                val chatId = it.document.get(FIRESTORE_CHAT_ID_NAME)
                                    .toString()
                                launch(Dispatchers.IO) {
                                    val chatInfoSnapshot = chatInfoFirestoreRef
                                        .whereEqualTo(
                                            FieldPath.documentId(),
                                            chatId
                                        )
                                        .get().await()

                                    val chatInfoFirebase =
                                        chatInfoSnapshot.documents.first()
                                            .toObject(ChatInfoFirebase::class.java)
                                            ?: throw RuntimeException("Impossible to convert this data snapshot into ChatInfoFirebase")
                                    val members =
                                        getMemberNameListFromUserQuery(chatId)
                                    val chatInfo = ChatInfo(
                                        chat_id = chatId,
                                        chat_name = chatInfoFirebase.chat_name,
                                        members = members,
                                        photo_url = chatInfoFirebase.photo_url
                                    )
                                    trySend(chatInfo)
                                }
                            }
                            Type.MODIFIED -> {
                                // TODO: add logic for changed data
                            }
                            Type.REMOVED -> {
                                // TODO: add logic when removing data
                            }
                        }
                    }
                }
            awaitClose()

        }

    }
}