package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response
import com.app.takenote.utility.Success
import com.app.takenote.utility.encodeString
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.KoinComponent
import org.koin.core.inject

open class AuthViewModel(protected val authRepository: AuthRepository) : BaseViewModel(), KoinComponent {
    private val _currentUserId: MutableLiveData<Response<String?>> = MutableLiveData()
    val currentUserId: LiveData<Response<String?>>
        get() = _currentUserId

    protected fun errorMessage(message: String) {
        try {
            _currentUserId.postValue(Error(message))
        } catch (exception: Exception) {

        }
    }

    protected fun currentUserId(userId: String?) = _currentUserId.postValue(Success(userId))
    open fun loginUser(email: String, password: String) {}
    open fun signUpUser(
        email: String,
        password: String,
        confirmPassword: String,
    ) {
    }

    override fun onCleared() {
        super.onCleared()
        authRepository.clearRegisterNetworkConnection()
    }
}