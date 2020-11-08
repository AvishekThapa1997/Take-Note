package com.app.takenote.viewmodels



import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.repository.DataRepository
import com.app.takenote.repository.ProfileRepository
import com.app.takenote.utility.*

class AddProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val dataRepository: DataRepository
) : BaseViewModel() {
    //    private val _imageUrl: MutableLiveData<Response<String>> = MutableLiveData()
//    val imageUrl: LiveData<Response<String>>
//        get() = _imageUrl
//    private val _currentUser: MutableLiveData<Response<User>> = MutableLiveData()
//    val currentUser: LiveData<Response<User>>
//        get() = _currentUser
//    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
//    val errorMessage: LiveData<String>
//        get() = _errorMessage


    fun uploadPhoto(imagePath: String, uid: String) {
        profileRepository.uploadImage(uid, imagePath, { imageUrl ->
            dataRepository.updateUserData(
                uid,
                mutableMapOf(IMAGE_URL to imageUrl)
            ) { updateError ->
                setImageUploadError(updateError)
            }
        }) { uploadError ->
            setImageUploadError(uploadError)
        }
    }

    fun setName(primaryId: String, name: String) {
        if (!name.isEmptyOrIsBlank()) {
            dataRepository.updateUserData(
                primaryId,
                mutableMapOf(FULL_NAME to name)
            ) { updateError ->
                setUpdateNameError(updateError)
            }
        } else {
            setUpdateNameError(FIELD_CANNOT_BE_EMPTY)
        }
    }

}