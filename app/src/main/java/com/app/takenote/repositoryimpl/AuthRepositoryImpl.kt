package com.app.takenote.repositoryimpl

import com.app.takenote.pojo.User
import com.app.takenote.repository.AuthRepository
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Executors

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val dataRepository: DataRepository
) : AuthRepository, BaseRepositoryImpl() {
    override suspend fun loginUser(
        email: String,
        password: String,
        success: (User) -> Unit,
        error: (String) -> Unit
    ) {
        if (isNetworkAvailable()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        currentUser?.let {
                            dataRepository.getCurrentUserData(it.uid, success, error)
                        }
                    }
                }.addOnFailureListener { exception ->
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> error(EMAIL_NOT_REGISTERED)
                        is FirebaseAuthInvalidCredentialsException -> error(INCORRECT_PASSWORD)
                        else -> error(SOMETHING_WENT_WRONG)
                    }
                }
        } else {
            error(NO_INTERNET_CONNECTION)
        }
    }

    override suspend fun signUpUser(
        email: String,
        password: String,
        success: (User) -> Unit,
        error: (String) -> Unit
    ) {
        if (isNetworkAvailable()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { signUpTask ->
                    if (signUpTask.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        currentUser?.let {
                            dataRepository.storeCurrentUserData(
                                it.uid,
                                email,
                                password,
                                success,
                                error
                            )
                        }
                    }
                }.addOnFailureListener { signUpException ->
                    when (signUpException) {
                        is FirebaseAuthUserCollisionException -> error(
                            EMAIL_IS_ALREADY_REGISTERED
                        )
                        else -> error(SOMETHING_WENT_WRONG)
                    }
                }
        } else {
            error(NO_INTERNET_CONNECTION)
        }
    }

    override fun clearRegisterNetworkConnection() {
        super.clearRegisterNetworkConnection()
    }
}