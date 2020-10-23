package com.app.takenote.utility

import com.app.takenote.extensions.invalidEmail
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.validPassword
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

object DataValidation {
    fun validateEmailAndPassword(email: String, password: String): String {
        if (email.isEmptyOrIsBlank() || password.isEmptyOrIsBlank())
            return FIELD_CANNOT_BE_EMPTY
        if (email.invalidEmail())
            return INVALID_EMAIL
        return VALID
    }

    fun validateUserCredentials(
        email: String,
        password: String,
        confirmPassword: String
    ): String {
        if (email.isEmptyOrIsBlank() || password.isEmptyOrIsBlank() || confirmPassword.isEmptyOrIsBlank())
            return FIELD_CANNOT_BE_EMPTY
        if (email.invalidEmail())
            return INVALID_EMAIL
//        if (!fullName.minLength(8))
//            return FULLNAME_SHOULD_CONTAIN_ATLEAST_EIGHT_CHARACTERS
        val passwordValidationMessage = password.validPassword()
        if (passwordValidationMessage != VALID_PASSWORD)
            return passwordValidationMessage
        if (password != confirmPassword)
            return PASSWORD_DID_NOT_MATCH
        return VALID
    }
}