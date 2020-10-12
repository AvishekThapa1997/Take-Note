package com.app.takenote.viewmodels

import com.app.takenote.extensions.runIO
import com.app.takenote.utility.DataValidation
import com.app.takenote.utility.VALID

class SignUpViewModel : AuthViewModel() {
    override fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        runIO {
            super.signUpUser(fullName, email, password, confirmPassword)
            val validationMessage =
                DataValidation.validateUserCredentials(fullName, email, password, confirmPassword)
            if (validationMessage != VALID)
                errorMessage(validationMessage)
        }
    }
}