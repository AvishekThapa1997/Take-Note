package com.app.takenote.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.utility.CustomError
import com.app.takenote.utility.ImageUploadError
import com.app.takenote.utility.NameUpdateError
import org.koin.core.KoinComponent


open class BaseViewModel : ViewModel(), KoinComponent {
    private val _errorMessage: MutableLiveData<CustomError> = MutableLiveData()
     val errorMessage: LiveData<CustomError>
        get() = _errorMessage

    protected fun setImageUploadError(errorMessage: String) {
        _errorMessage.value = ImageUploadError(errorMessage)
    }

    protected fun setUpdateNameError(message: String) {
        _errorMessage.value = NameUpdateError(message)
    }
}