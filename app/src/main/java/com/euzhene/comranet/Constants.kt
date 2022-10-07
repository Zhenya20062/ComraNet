package com.euzhene.comranet

import androidx.compose.ui.graphics.Color
import com.euzhene.comranet.chatRoom.data.paging.PagingDataSourceImpl
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

//<--------------References---------------->
val firebaseDatabase =
    FirebaseDatabase.getInstance(firebaseDatabaseApi) //your api key should be here, in parentheses (like https://xyz.europe-west1.firebasedatabase.app/)
val firebaseChatReference = firebaseDatabase.getReference("chats")
val firebaseLastFirebaseDataReference = firebaseDatabase.getReference("last_message")

val queryByName =
    firebaseChatReference.orderByChild("timestamp").limitToLast(PagingDataSourceImpl.PAGE_SIZE)

private val firebaseStorageRef = Firebase.storage(firebaseStorageApi).reference
val imageStorage = firebaseStorageRef.child("images/")
val chatImageStorage = firebaseStorageRef.child("chats_photo/")
val userImageStorage = firebaseStorageRef.child("users_photo/")

val userReference = firebaseDatabase.getReference("users")


//<------------VALUES FOR CONFIG------------->
const val DATA_STORE_NAME = "settings"
const val CONFIG_NAME = "config"
const val FONT_SIZE_DEFAULT_VALUE = 19f
val RECEIVER_MESSAGE_VALUE = Color(0xff03b1fc)
val SENDER_MESSAGE_VALUE = Color(0xffd1f1ff)
val APP_BAR_VALUE = Color(0xff69059c)
val ICON_SECTION_VALUE = Color.Blue.copy(alpha = 0.4f)
val MESSAGE_USERNAME_VALUE = Color.Blue
val MESSAGE_TEXT_VALUE = Color.Black
val MESSAGE_DATE_VALUE = Color.Black
val DATE_DIVIDER_BACKGROUND_VALUE = Color.Black
val DATE_DIVIDER_TEXT_VALUE = Color.White


//<-------------LOG--------------------->
const val TAG_PRESENT = "PresentationLayer"
const val TAG_DATA = "DataLayer"
const val TAG_DOMAIN = "DomainLayer"