package com.app.takenote.viewmodels

import com.app.takenote.extensions.runIO
import com.app.takenote.utility.DataValidation
import com.app.takenote.utility.VALID

class LoginViewModel : AuthViewModel() {
    //    val user : LiveData<Response<User>>
//    get() = super.registeredUser
    override fun loginUser(email: String, password: String) {
        runIO {
            val message = DataValidation.validateEmailAndPassword(email, password)
            if (message != VALID)
                errorMessage(message)
        }
    }
}