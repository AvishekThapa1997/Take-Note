package com.app.takenote.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.takenote.repository.AuthRepository
import com.app.takenote.repository.DataRepository
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.repositoryimpl.AuthRepositoryImpl
import com.app.takenote.utility.*
import org.koin.core.inject

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository,
) : BaseViewModel() {
    private val authRepository: AuthRepository by inject<AuthRepositoryImpl>()
    private val _logout: MutableLiveData<String> = MutableLiveData()
    val logout: LiveData<String>
        get() = _logout

    fun updateProfilePhoto(primaryId: String, imageUrl: String) {
        profileRepository.uploadImage(primaryId, imageUrl, { updatedImageUrl ->
            dataRepository.updateUserData(
                primaryId,
                mutableMapOf(IMAGE_URL to updatedImageUrl)
            ) { updatedError ->
                setImageUploadError(updatedError)
            }
        }, { uploadError ->
            setImageUploadError(uploadError)
        })
    }

    fun updateName(primaryId: String, updatedName: String) {
        dataRepository.updateUserData(
            primaryId,
            mutableMapOf(FULL_NAME to updatedName)
        ) { errorMessage ->
            setUpdateNameError(errorMessage)
        }
    }

    fun logoutUser() {
        authRepository.logoutUser()
        _logout.value = ""
    }
}