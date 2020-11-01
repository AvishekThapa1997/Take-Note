package com.app.takenote.viewmodels


import com.app.takenote.extensions.runIO
import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.*

class LoginViewModel(authRepository: AuthRepository) : AuthViewModel(authRepository) {
    //    val user : LiveData<Response<User>>
//    get() = super.registeredUser
    override fun loginUser(email: String, password: String) {
        runIO {
            val message = DataValidation.validateEmailAndPassword(email, password)
            if (message != VALID) {
                errorMessage(message)
            } else {
                //val encryptPassword = encodeString(password)
                signInUser(email,password)
            }
        }
    }

    private suspend fun signInUser(email: String, password: String) {
        authRepository.loginUser(email, password, { userId: String? ->
            currentUserId(userId)
        }, { error ->
            errorMessage(error)
        })
    }
}