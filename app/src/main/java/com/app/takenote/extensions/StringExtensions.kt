package com.app.takenote.extensions

import com.app.takenote.utility.*
import com.wajahatkarim3.easyvalidation.core.view_ktx.*

fun String?.isEmptyOrIsBlank() = this.isNullOrBlank() && this.isNullOrEmpty()

fun String.invalidEmail() = !this.validEmail()

fun String.validPassword(): String {
    if (!this.atleastOneUpperCase())
        return PASSWORD_SHOULD_CONTAIN_ATLEAST_ONE_UPPERCASE
    if (!this.atleastOneLowerCase())
        return PASSWORD_SHOULD_CONTAIN_ATLEAST_ONE_LOWERCASE
    if (!this.atleastOneSpecialCharacters())
        return PASSWORD_SHOULD_CONTAIN_ATLEAST_ONE_SPECIAL_CHARACTERS
    if (!this.atleastOneNumber())
        return PASSWORD_SHOULD_CONTAIN_ATLEAST_ONE_DIGIT
    if (!this.minLength(8))
        return PASSWORD_SHOULD_OF_MINIMUM_EIGHT_CHARACTERS
    return VALID_PASSWORD
}