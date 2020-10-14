package com.app.takenote.viewmodels


import android.util.Log
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.inject

class SignUpViewModel : AuthViewModel() {
    override fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        runIO {
            val validationMessage =
                DataValidation.validateUserCredentials(fullName, email, password, confirmPassword)
            if (validationMessage != VALID)
                errorMessage(validationMessage)
            else
                createUser(fullName, email, password)

        }
    }

    private fun createUser(fullName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = firebaseAuth.currentUser?.uid
                storeData(fullName, email, password, uid)
            }
        }.addOnFailureListener { exception ->
            when (exception) {
                is FirebaseAuthUserCollisionException -> errorMessage(EMAIL_IS_ALREADY_REGISTERED)
                else -> errorMessage(SOMETHING_WENT_WRONG)
            }
        }
    }

    private fun storeData(fullName: String, email: String, password: String, id: String?) {
        val primaryId = uniqueId(id,email)
        val encryptPassword = encodeString(password)
        val dataToStore = mutableMapOf(
            FULL_NAME to fullName,
            EMAIL to email, PASSWORD to encryptPassword
        )
        fireStore.collection(COLLECTION_NAME).document(primaryId).set(dataToStore).addOnCompleteListener {task ->
            if(task.isSuccessful)
               currentUser(User(fullName,email))
        }.addOnFailureListener{ exception ->
            Log.i("TAG", "${exception.message}")
        }
    }
}