package com.app.takenote.viewmodels


import android.util.Log
import com.app.takenote.extensions.runIO
import com.app.takenote.repository.AuthRepository
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.*


class SignUpViewModel(
    authRepository: AuthRepository,
    private val dataRepository: DataRepository
) :
    AuthViewModel(authRepository) {
    private var observing = false
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
                //val encryptPassword = encodeString(password)
                createUser(email,password)
            }

        }
    }

    private suspend fun createUser(email: String, password: String) {
        authRepository.signUpUser(email, password, { currentUserId ->
            currentUserId(currentUserId)
            val userData =
                mutableMapOf(
                    EMAIL to email,
                    FULL_NAME to "",
                    IMAGE_URL to "",
                    PRIMARY_ID to currentUserId!!
                )
            dataRepository.storeCurrentUserData(
                userData, currentUserId
            ) { error: String ->
                errorMessage(error)
            }
        }, { signInError ->
            errorMessage(signInError)
        })
    }
}