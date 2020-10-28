package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.User
import com.app.takenote.repository.DataRepository
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.*
import com.google.firebase.firestore.util.Executors
import java.io.File
import java.io.FileInputStream

class AddProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository
) : ViewModel() {
    private val _imageUrl: MutableLiveData<Response<String>> = MutableLiveData()
    val imageUrl: LiveData<Response<String>>
        get() = _imageUrl
    private val _currentUser: MutableLiveData<Response<User>> = MutableLiveData()
    val currentUser: LiveData<Response<User>>
        get() = _currentUser

    fun uploadPhoto(imagePath: String, uid: String) {
        runIO {
            profileRepository.uploadImage(uid, imagePath, { imageUrl ->
                _imageUrl.postValue(Success(imageUrl))
                dataRepository.updateData(
                    uid,
                    mutableMapOf(IMAGE_URL to imageUrl),
                    null,
                    { uploadErrorMessage ->
                        _imageUrl.postValue(Error(uploadErrorMessage))
                    })
            }, { error ->
                _imageUrl.postValue(Error(error))
            })
        }
    }

    fun setName(primaryId: String, name: String) {
        runIO {
            if (!name.isEmptyOrIsBlank()) {
                dataRepository.updateData(
                    primaryId,
                    mutableMapOf(FULL_NAME to name),
                    { user ->
                        _currentUser.postValue(Success(user))
                    },
                    { error ->
                        _currentUser.postValue(Error(error))
                    })
            } else {
                _currentUser.postValue(Error(FIELD_CANNOT_BE_EMPTY))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        profileRepository.clearRegisterNetworkConnection()
        dataRepository.clearRegisterNetworkConnection()
    }
}