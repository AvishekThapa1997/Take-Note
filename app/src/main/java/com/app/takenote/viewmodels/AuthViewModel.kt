package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.pojo.User
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response
import com.app.takenote.utility.Success
import com.app.takenote.utility.encodeString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.KoinComponent
import org.koin.core.inject

open class AuthViewModel : ViewModel(), KoinComponent {
    private val registeredUser: MutableLiveData<Response<User>> = MutableLiveData()
    protected val firebaseAuth: FirebaseAuth by inject()
    protected val fireStore: FirebaseFirestore by inject()
    val currentUser: LiveData<Response<User>>
        get() = registeredUser

    protected fun errorMessage(message: String) = registeredUser.postValue(Error(message))
    protected fun currentUser(user : User) = registeredUser.postValue(Success(user))
    open fun loginUser(email: String, password: String) {}
    open fun signUpUser(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
    }

    protected fun uniqueId(uid: String?, email: String): String {
        return if (!uid.isNullOrEmpty() && !uid.isNullOrBlank()) {
            uid
        } else {
            encodeString(email)
        }
    }
}