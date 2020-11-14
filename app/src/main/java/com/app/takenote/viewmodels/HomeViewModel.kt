package com.app.takenote.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.takenote.repository.DataRepository

class HomeViewModel(private val dataRepository: DataRepository) : BaseViewModel() {
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String>
        get() = _message

    fun deleteNote(noteId: String) {
        dataRepository.deleteNote(noteId) { errorMessage ->
            _message.value = errorMessage
        }
    }
}