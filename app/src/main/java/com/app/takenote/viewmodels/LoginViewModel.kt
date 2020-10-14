package com.app.takenote.viewmodels

import android.util.Log
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : AuthViewModel() {
    //    val user : LiveData<Response<User>>
//    get() = super.registeredUser
    override fun loginUser(email: String, password: String) {
        runIO {
            val message = DataValidation.validateEmailAndPassword(email, password)
            if (message != VALID)
                errorMessage(message)
            else
                signInUser(email, password)
        }
    }

    private fun signInUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = firebaseAuth.currentUser?.uid
                getFullName(uid, email)
            }
        }.addOnFailureListener { exception ->
            when (exception) {
                is FirebaseAuthInvalidUserException -> errorMessage(EMAIL_NOT_REGISTERED)
                is FirebaseAuthInvalidCredentialsException -> errorMessage(INCORRECT_PASSWORD)
                else -> errorMessage(SOMETHING_WENT_WRONG)
            }

        }
    }

    private fun getFullName(uid: String?, email: String) {
        val primaryId = uniqueId(uid, email)
        fireStore.collection(COLLECTION_NAME).document(primaryId).get()
            .addOnSuccessListener { document ->
                document?.let { documentSnapshot ->
                    val fullName = documentSnapshot[FULL_NAME] as String
                    val userEmail = documentSnapshot[EMAIL] as String
                    currentUser(User(fullName, userEmail))
                }
            }.addOnFailureListener {
                errorMessage(SOMETHING_WENT_WRONG)
            }
    }
}