package com.app.takenote.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.Note
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.NOTE_DISCARDED
import java.util.*


class NoteUploadViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String>
        get() = _message

    fun uploadNote(noteTitle: String, noteBody: String, userId: String) {
        runIO {
            //var isSuccess = true
            if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank())
                _message.value = NOTE_DISCARDED
            else {
                val note = Note(id = "", noteTitle, noteBody, userId, Date().time.toString())
                dataRepository.storeNote(note) { errorMessage ->
                    _message.value = errorMessage
                    //isSuccess = false
                }
               // setSuccess(isSuccess)
            }
        }
    }

    fun updateNote(noteTitle: String, noteBody: String, note: Note) {
        runIO {
           // var isSuccess = true
            if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank())
                _message.value = NOTE_DISCARDED
            else {
                val updatedNote = note.copy(title = noteTitle, body = noteBody)
                dataRepository.updateNote(updatedNote) { errorMessage ->
                    _message.value = errorMessage
                   // isSuccess = false
                }
                //setSuccess(isSuccess)
            }
        }
    }

//    private fun setSuccess(success: Boolean) {
//        if (success)
//            _message.value = SUCCESS
//        else
//            _message.value = FAILS
//    }
}