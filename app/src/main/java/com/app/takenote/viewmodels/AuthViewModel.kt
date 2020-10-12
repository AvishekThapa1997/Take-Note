package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.pojo.User
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response

open class AuthViewModel : ViewModel() {
    private val registeredUser: MutableLiveData<Response<User>> = MutableLiveData()
    val currentUser: LiveData<Response<User>>
        get() = registeredUser

    protected fun errorMessage(message: String) = registeredUser.postValue(Error(message))
    open fun loginUser(email: String, password: String) {}
    open fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
    }
}