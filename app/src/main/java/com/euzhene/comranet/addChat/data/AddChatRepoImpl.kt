package com.euzhene.comranet.addChat.data

import androidx.core.net.toUri
import com.euzhene.comranet.FIRESTORE_LOGIN_NAME
import com.euzhene.comranet.FIRESTORE_USER_ID_NAME
import com.euzhene.comranet.addChat.domain.AddChatRepo
import com.euzhene.comranet.addChat.domain.entity.ChatInfo
import com.euzhene.comranet.addChat.domain.entity.ChatInfoSendFirestore
import com.euzhene.comranet.addChat.domain.entity.ChatMember
import com.euzhene.comranet.addChat.domain.entity.UserInfo
import com.euzhene.comranet.util.Response
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class AddChatRepoImpl(
    private val userFirestoreRef: CollectionReference,
    private val chatInfoFirestoreRef: CollectionReference,
    private val chatMembersFirestoreRef: CollectionReference,
    //  private val rtdRef: DatabaseReference,
    //private val userReference: DatabaseReference,
    private val storageRef: StorageReference,
) : AddChatRepo {
    override fun createChat(chatInfo: ChatInfo, userLogins: List<String>): Flow<Response<Unit>> {
        return callbackFlow {
            trySend(Response.Loading(Unit))
            var chatPhoto: String? = null
            if (chatInfo.photo_url != null) {
                val uploadTask = storageRef.child(UUID.randomUUID().toString())
                    .putFile(chatInfo.photo_url.toUri()).await()
                if (!uploadTask.task.isSuccessful) {
                    trySend(
                        Response.Error(
                            uploadTask.task.exception?.localizedMessage ?: "Storage error"
                        )
                    )
                }
                chatPhoto = uploadTask.storage.downloadUrl.await().toString()
            }

            val chatInfoFirebase = ChatInfo(
                chat_id = UUID.randomUUID().toString(),
                chat_name = chatInfo.chat_name,
                members = chatInfo.members,
                photo_url = chatPhoto
            )
            chatInfoFirestoreRef.document(chatInfoFirebase.chat_id).set(
                ChatInfoSendFirestore(
                    chat_name = chatInfo.chat_name,
                    photo_url = chatInfoFirebase.photo_url
                )
            ).addOnCompleteListener {
                if (!it.isSuccessful) {
                    trySend(Response.Error(it.exception?.localizedMessage ?: "Firestore error"))
                } else {
                    userFirestoreRef
                        .whereIn(FIRESTORE_LOGIN_NAME, userLogins)
                        .get().addOnCompleteListener {
                            if (!it.isSuccessful) {
                                //todo: delete added chatInfo
                                trySend(
                                    Response.Error(
                                        it.exception?.localizedMessage ?: "Firestore error"
                                    )
                                )
                            } else {
                                val userIdList = it.result!!.documents.map {
                                    it.id
                                }
                                userIdList.forEachIndexed { i, s ->
                                    chatMembersFirestoreRef.add(
                                        ChatMember(
                                            chat_id = chatInfoFirebase.chat_id,
                                            user_id = s
                                        )
                                    ).addOnCompleteListener {
                                        if (!it.isSuccessful) {
                                            //todo delete added chatInfo
                                            trySend(
                                                Response.Error(
                                                    it.exception!!.localizedMessage
                                                        ?: "Firestore error"
                                                )
                                            )
                                        } else if (userIdList.lastIndex == i) {
                                            trySend(Response.Success(Unit))
                                        }
                                    }
                                }

                            }
                        }

                }
            }
            //   val chatId = rtdRef.child("chats").push().key!!
            //       child("chat_info").setValue(chatInfoFirebase)
            // .addOnCompleteListener {
            //    if (it.isSuccessful) {

//            userLogins.forEachIndexed { i, s ->
//                userReference.child(s).child("chats").child(chatId).setValue(chatInfoFirebase)
//                    .apply {
//                        if (i == userLogins.lastIndex) {
//                            addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    trySend(Response.Success(Unit))
//                                } else trySend(
//                                    Response.Error(
//                                        it.exception?.localizedMessage ?: "Firebase Error"
//                                    )
//                                )
//                            }
//                        }
//                    }
//            }
            awaitClose()
        }

    }

    override suspend fun getAllUsers(): Flow<Response<List<UserInfo>>> {
        return callbackFlow {
            userFirestoreRef.get().addOnCompleteListener {
                if (!it.isSuccessful) {
                    trySend(Response.Error(it.exception!!.localizedMessage?:"Firestore error"))
                } else {
                    val userInfoList = it.result!!.toObjects(UserInfo::class.java)
                    trySend(Response.Success(userInfoList))
                }
            }
            awaitClose()
        }
//
//        val result = rtdRef.child("users").get().await()
//        val usernames = result.children.mapNotNull {
//            val username = it.child("username").getValue(String::class.java)!!
//            val photo = it.child("photo").getValue(String::class.java)
//            UserInfo(username = username, login = it.key!!, photo_url = photo)
//        }
//
//        return usernames
    }
}