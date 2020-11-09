package com.app.takenote.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.Note
import com.app.takenote.repository.DataRepository
import java.util.*


class NoteUploadViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun uploadNote(noteTitle: String, noteBody: String, userId: String) {
        runIO {
            if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank())
                _errorMessage.value = "Note Discarded"
            else {
                val note = Note(id = "", noteTitle, noteBody, userId, Date().time.toString())
                dataRepository.storeNote(note) { errorMessage ->
                    _errorMessage.value = errorMessage
                }
            }
        }
    }

    fun updateNote(noteTitle: String, noteBody: String, note: Note) {
        runIO {
            if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank())
                _errorMessage.value = "Note Discarded"
            else {
                val updatedNote = note.copy(title = noteTitle, body = noteBody)
                dataRepository.updateNote(updatedNote) { errorMessage ->
                    _errorMessage.value = errorMessage
                }
            }
        }
    }
}