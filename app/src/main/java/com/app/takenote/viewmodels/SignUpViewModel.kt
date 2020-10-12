package com.app.takenote.viewmodels

import android.util.Log
import com.app.takenote.extensions.runIO
import com.app.takenote.utility.DataValidation
import com.app.takenote.utility.VALID
import com.google.firebase.auth.FirebaseAuthUserCollisionException

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
                createUser(email, password)

        }
    }

    private  fun createUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("TAG", "createUser: ${firebaseAuth.currentUser}")
            }
        }.addOnFailureListener { exception ->
            when (exception) {
                is FirebaseAuthUserCollisionException -> errorMessage("Email is Already is registerd")
                else -> errorMessage("Something went wrong")
            }
        }
    }
}