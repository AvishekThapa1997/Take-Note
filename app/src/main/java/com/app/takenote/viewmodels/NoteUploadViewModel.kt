package com.app.takenote.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.extensions.runIO
import com.app.takenote.pojo.Note
import com.app.takenote.repository.DataRepository
import com.app.takenote.utility.DateUtil
import com.app.takenote.utility.NOTE_DISCARDED
import com.app.takenote.utility.REMINDER_TIME


class NoteUploadViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String>
        get() = _message

    fun uploadNote(noteTitle: String, noteBody: String, userId: String, reminderTime: String) {
        runIO {
            //var isSuccess = true
            if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank())
                _message.value = NOTE_DISCARDED
            else {
                val note = Note(
                    id = "",
                    noteTitle,
                    noteBody,
                    userId,
                    "${DateUtil.currentTime}",
                    reminderTime
                )
                dataRepository.storeNote(note) { errorMessage ->
                    _message.value = errorMessage
                    //isSuccess = false
                }
                // setSuccess(isSuccess)
            }
        }
    }

    fun updateNote(updatedData: Map<String, String>, note: Note) {
        runIO {
            // var isSuccess = true
            if (updatedData.isNullOrEmpty())
                _message.value = NOTE_DISCARDED
            else {
                dataRepository.updateNote(updatedData, note.id) { errorMessage ->
                    _message.value = errorMessage
                    // isSuccess = false
                }
                //setSuccess(isSuccess)
            }
        }
    }

    fun deleteTime(note: Note) {
        runIO {
            dataRepository.updateNote(mutableMapOf(REMINDER_TIME to ""), note.id) { errorMessage ->
                _message.value = errorMessage
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