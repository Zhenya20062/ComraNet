package com.euzhene.comranet.autorization.data

import com.euzhene.comranet.FIRESTORE_EMAIL_NAME
import com.euzhene.comranet.FIRESTORE_LOGIN_NAME
import com.euzhene.comranet.FIRESTORE_NOTIFICATION_ID_NAME
import com.euzhene.comranet.autorization.domain.entity.UserInfoFirestore
import com.euzhene.comranet.autorization.domain.entity.UserLoginData
import com.euzhene.comranet.autorization.domain.entity.UserRegistrationData
import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.onesignal.OneSignal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*

class AuthRepoImpl(
    private val userFirestoreRef: CollectionReference,
    private val storageReference: StorageReference,
) : AuthRepo {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun registerUser(data: UserRegistrationData): Flow<Response<FirebaseUser>> {
        return flow {
            try {
                emit(Response.Loading())
                emit(createUser(data))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }
        }

    }

    override suspend fun logInUser(data: UserLoginData): Flow<Response<FirebaseUser>> {
        return flow {
            try {
                emit(Response.Loading())
                if (data.email == null) {
                    emit(loginViaLogin(data))
                } else {
                    emit(loginViaEmail(data))
                }
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))

            }
        }

    }

    override fun isSignIn(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun updateNotificationId(): Response<Unit> {
        try {
            if (auth.currentUser == null) throw RuntimeException("FirebaseUser not found")
            val deviceState = OneSignal.getDeviceState()
                ?: throw RuntimeException("Device state is null")
            userFirestoreRef.document(auth.currentUser!!.uid)
                .update(FIRESTORE_NOTIFICATION_ID_NAME, deviceState.userId)
                .await()
            return Response.Success(Unit)
//            userRef.get().await().children.forEach {
//                if (it.key == auth.currentUser!!.uid) {
//
//
//                    userRef.child(it.key!!).child("notification_id").setValue(deviceState.userId)
//                        .await()
//                    return Response.Success(Unit)
//                }
//            }
            // return Response.Error("No result")
        } catch (e: Exception) {
            return Response.Error(e.message.toString())
        }

    }


    private suspend fun loginViaLogin(data: UserLoginData): Response<FirebaseUser> {
        val snapshot = userFirestoreRef
            .whereEqualTo(FIRESTORE_LOGIN_NAME, data.login)
            .get().await()

        val userEmail = snapshot.documents.first().get(FIRESTORE_EMAIL_NAME, String::class.java)
            ?: throw RuntimeException("Database leak")
        return loginViaEmail(data.copy(email = userEmail))

//        userRef.get().await().children.forEach {
//            if (it.child("login").getValue<String>() == data.login) {
//                val email =
//                    it.child("email").getValue<String>() ?: return Response.Error("Database leak")
//                return loginViaEmail(data.copy(email = email))
//            }
//        }
//        return Response.Error("Login not found")
    }

    private suspend fun loginViaEmail(data: UserLoginData): Response<FirebaseUser> {
        val result = auth.signInWithEmailAndPassword(data.email!!, data.password).await()
        return Response.Success(result.user!!)
    }

    private suspend fun createUser(
        data: UserRegistrationData
    ): Response<FirebaseUser> {

        val loginSnapshot = userFirestoreRef
            .whereEqualTo(FIRESTORE_LOGIN_NAME, data.login)
            .get().await()
        if (!loginSnapshot.isEmpty) {
            return Response.Error("This login is taken")
        }
        val user = auth.createUserWithEmailAndPassword(data.email, data.password).await().user
        updateProfile(user!!, data.username)

        var photoUrl: String? = null
        if (data.photoUri != null) {
            val result = storageReference.child(UUID.randomUUID().toString()).putFile(data.photoUri)
                .await()
            if (!result.task.isSuccessful) {
                //  user.delete().await()
                return Response.Error(result.task.exception?.localizedMessage ?: "Storage error")
            }
            photoUrl = result.storage.downloadUrl.await().toString()


            // child("photo").setValue(photoUrl)
        }
        userFirestoreRef.document(user.uid).set(
            UserInfoFirestore(
                email = data.email,
                login = data.login,
                photo_url = photoUrl,
                username = data.username,
                notification_id = "",
            )
        ).await()


//        userRef.child(user.uid).apply {
////            if (get().await().exists()) {
////                user.delete().await()
////                return Response.Error("This login is taken")
////            }
//
//
//            child("username").setValue(data.username).await()
//            child("email").setValue(data.email).await()
//            child("login").setValue(data.login).await()
//        }

        return Response.Success(user)
    }

    private suspend fun updateProfile(
        user: FirebaseUser,
        username: String,
    ) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        ).await()
    }
}