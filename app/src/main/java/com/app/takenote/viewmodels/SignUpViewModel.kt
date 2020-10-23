package com.app.takenote.viewmodels


import android.util.Log
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.User
import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.*
import com.google.firebase.firestore.util.Executors


class SignUpViewModel(authRepository: AuthRepository) : AuthViewModel(authRepository) {
    override fun signUpUser(
        email: String,
        password: String,
        confirmPassword: String,
    ) {
        runIO {
            val validationMessage =
                DataValidation.validateUserCredentials(email, password, confirmPassword)
            if (validationMessage != VALID)
                errorMessage(validationMessage)
            else {
                createUser(email,password)
            }

        }
    }

    private suspend fun createUser(email: String, password: String) {
        authRepository.signUpUser(email, password, { user ->
            currentUser(user)
        }, { error ->
            errorMessage(error)
        })
    }

}