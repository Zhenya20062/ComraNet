package com.euzhene.comranet.autorization.data

import android.net.Uri
import com.euzhene.comranet.autorization.domain.entity.UserLoginData
import com.euzhene.comranet.autorization.domain.entity.UserRegistrationData
import com.euzhene.comranet.autorization.domain.repo.AuthRepo
import com.euzhene.comranet.util.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepoImpl(
    private val userRef: DatabaseReference,
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

    private suspend fun loginViaLogin(data: UserLoginData): Response<FirebaseUser> {
        userRef.child(data.login!!).get().await().apply {
            if (!exists()) return Response.Error("Login not found")
            val email = child("email").getValue<String>() ?: return Response.Error("Database leak")
            return loginViaEmail(data.copy(email = email))
        }
    }

    private suspend fun loginViaEmail(data: UserLoginData): Response<FirebaseUser> {
        val result = auth.signInWithEmailAndPassword(data.email!!, data.password).await()
        return Response.Success(result.user!!)
    }

    private suspend fun createUser(
        data: UserRegistrationData
    ): Response<FirebaseUser> {
        userRef.child(data.login).apply {
            if (get().await().exists()) return Response.Error("This login is taken")
            child("username").setValue(data.username).await()
            child("email").setValue(data.email).await()
        }
        val result = auth.createUserWithEmailAndPassword(data.email, data.password).await()
        setPhotoAndName(result.user!!, data.photoUri, data.username)
        return Response.Success(result.user!!)
    }

    private suspend fun setPhotoAndName(
        user: FirebaseUser,
        photo: Uri?,
        username: String,
    ) {
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setPhotoUri(photo)
                .setDisplayName(username)
                .build()
        ).await()
    }
}