package com.euzhene.comranet

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

private const val CHAT_INFO_PATH = "chat_info"
private const val MESSAGES_PATH = "messages"
private const val USERS_PATH = "users"
private const val CHAT_MEMBERS_PATH = "chat_members"

const val FIRESTORE_TIMESTAMP_NAME = "timestamp"
const val FIRESTORE_CHAT_ID_NAME = "chat_id"
const val FIRESTORE_MESSAGE_ID_NAME = "message_id"
const val FIRESTORE_SENDER_ID_NAME = "sender_id"
const val FIRESTORE_TYPE_NAME = "type"
const val FIRESTORE_DATA_NAME = "data"
const val FIRESTORE_USER_ID_NAME = "user_id"

const val FIRESTORE_USERNAME = "username"
const val FIRESTORE_LOGIN_NAME = "login"
const val FIRESTORE_NOTIFICATION_ID_NAME = "notification_id"
const val FIRESTORE_EMAIL_NAME="email"
const val FIRESTORE_PHOTO_URL_NAME="photo_url"

//<--------------References---------------->
val firebaseDatabase =
    FirebaseDatabase.getInstance(firebaseDatabaseApi) //your api key should be here, in parentheses (like https://xyz.europe-west1.firebasedatabase.app/)
val firebaseChatReference = firebaseDatabase.getReference("chats")

private val firestore = Firebase.firestore

fun messagesFirestore(): CollectionReference {
    return firestore.collection(MESSAGES_PATH)
}

fun chatInfoFirestore(): CollectionReference {
    return firestore.collection(CHAT_INFO_PATH)
}

fun usersFirestore(): CollectionReference {
    return firestore.collection(USERS_PATH)
}

fun chatMembersFirestore(): CollectionReference {
    return firestore.collection(CHAT_MEMBERS_PATH)
}

suspend fun getMemberNameListFromUserQuery(chatId: String): List<String> {
    val snapshot = chatMembersFirestore()
        .whereEqualTo(FIRESTORE_CHAT_ID_NAME, chatId)
        .get().await()

    return snapshot.documents.map {
        val senderId = it.get(FIRESTORE_USER_ID_NAME, String::class.java)!!
        val username = getSenderNameFromUserQuery(senderId)
        username
    }
}

suspend fun getSenderNameFromUserQuery(senderId: String): String {
    val snapshot = usersFirestore()
        .whereEqualTo(FieldPath.documentId(), senderId)
        .get().await()
    return snapshot.documents.first().get(FIRESTORE_USERNAME, String::class.java)!!
}

fun chatMessages(chatId: String): Query {
    return firebaseChatReference.child(chatId).child("messages").orderByChild("timestamp")
        .limitToLast(10)
}

//    firebaseChatReference

private val firebaseStorageRef = Firebase.storage(firebaseStorageApi).reference
val imageStorage = firebaseStorageRef.child("images/")
val chatImageStorage = firebaseStorageRef.child("chats_photo/")
val userImageStorage = firebaseStorageRef.child("users_photo/")

val userReference = firebaseDatabase.getReference("users")
fun getMyUserRef(id: String) = userReference.child(id)
fun getUserGroup(userId: String, groupId: String) =
    userReference.child(userId).child("chats").child(groupId)

//<-------------LOG--------------------->
const val TAG_PRESENT = "PresentationLayer"
const val TAG_DATA = "DataLayer"
const val TAG_DOMAIN = "DomainLayer"

//<-----------OneSignal------------>
const val ONE_SIGNAL_KEY = oneSignalApiKey //put your app id from OneSignal here