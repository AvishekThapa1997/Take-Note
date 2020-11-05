package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.takenote.pojo.User
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.Error
import com.app.takenote.utility.Response
import com.app.takenote.utility.Success
import com.google.firebase.firestore.Source

class SplashViewModel(private val dataRepository: DataRepository) : BaseViewModel() {
    private val _currentUser: MutableLiveData<Response<User>> = MutableLiveData()
    val currentUser: LiveData<Response<User>>
        get() = _currentUser

    fun currentUser(primaryId: String) {
        dataRepository.getCurrentUserData(primaryId, { currentUser ->
            _currentUser.value = Success(currentUser)
        }, { error ->
            _currentUser.value = Error(error)
        }, Source.CACHE)
    }
}