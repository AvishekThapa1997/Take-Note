package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.takenote.repository.AuthRepository
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response
import com.app.takenote.utility.Success
import org.koin.core.KoinComponent

open class AuthViewModel(protected val authRepository: AuthRepository) : BaseViewModel(),
    KoinComponent {
    private val _currentUserId: MutableLiveData<Response<String?>> = MutableLiveData()
    val currentUserId: LiveData<Response<String?>>
        get() = _currentUserId

    protected fun errorMessage(message: String) {
        _currentUserId.value = Error(message)
    }

    protected fun currentUserId(userId: String?) {
        _currentUserId.value = Success(userId)
    }

    open fun loginUser(email: String, password: String) {}
    open fun signUpUser(
        email: String,
        password: String,
        confirmPassword: String,
    ) {
    }
}