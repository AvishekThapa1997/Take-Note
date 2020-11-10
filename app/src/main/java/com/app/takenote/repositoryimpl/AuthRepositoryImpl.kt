package com.app.takenote.repositoryimpl


import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore : FirebaseFirestore
) : AuthRepository {
    override suspend fun loginUser(
        email: String,
        password: String,
        success: (String?) -> Unit,
        error: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { loginTask ->
                if (loginTask.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    success(currentUser?.uid)
                } else {
                    error(SOMETHING_WENT_WRONG)
                }
            }.addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException -> error(EMAIL_NOT_REGISTERED)
                    is FirebaseAuthInvalidCredentialsException -> error(INCORRECT_PASSWORD)
                    else -> error(SOMETHING_WENT_WRONG)
                }
            }
    }

    override suspend fun signUpUser(
        email: String,
        password: String,
        success: (String?) -> Unit,
        error: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { signUpTask ->
                if (signUpTask.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    success(currentUser?.uid)
                }
            }.addOnFailureListener { signUpException ->
                when (signUpException) {
                    is FirebaseAuthUserCollisionException -> error(
                        EMAIL_IS_ALREADY_REGISTERED
                    )
                    else -> error(SOMETHING_WENT_WRONG)
                }
            }
    }

    override fun logoutUser() {
        firebaseAuth.signOut()
        fireStore.clearPersistence()
    }
}