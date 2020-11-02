package com.app.takenote.viewmodels


import com.app.takenote.repository.DataRepository
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.*

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository
) : BaseViewModel() {

    fun updateProfilePhoto(primaryId: String, imageUrl: String) {
        profileRepository.uploadImage(primaryId, imageUrl, { updatedImageUrl ->
            dataRepository.updateData(
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
        dataRepository.updateData(
            primaryId,
            mutableMapOf(FULL_NAME to updatedName)
        ) { errorMessage ->
            setUpdateNameError(errorMessage)
        }
    }

}