package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.pojo.User
import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response
import com.app.takenote.utility.Success
import com.app.takenote.utility.encodeString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import org.koin.core.KoinComponent
import org.koin.core.inject

open class AuthViewModel(protected val authRepository: AuthRepository) : ViewModel(), KoinComponent {
    private val _currentUser: MutableLiveData<Response<User>> = MutableLiveData()
    protected val firebaseAuth: FirebaseAuth by inject()
    protected val fireStore: FirebaseFirestore by inject()
    protected val storageReference: StorageReference by inject()
    val currentUser: LiveData<Response<User>>
        get() = _currentUser

    protected fun errorMessage(message: String) {
        try {
            _currentUser.postValue(Error(message))
        } catch (exception: Exception) {

        }
    }

    protected fun currentUser(user: User) = _currentUser.postValue(Success(user))
    open fun loginUser(email: String, password: String) {}
    open fun signUpUser(
        email: String,
        password: String,
        confirmPassword: String,
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