package com.app.takenote.ui


import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.app.takenote.R
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.NoteUploadViewModel
import kotlinx.android.synthetic.main.activity_note_take.*
import org.koin.android.viewmodel.ext.android.viewModel


class NoteUploadActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_note_take
    override fun enabledFullScreen() = true
    private var isKeyboardVisible = false
    private val noteUploadViewModel: NoteUploadViewModel by viewModel()
    private var currentUser: User? = null
    private var currentNote: Note? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        if (intent.hasExtra(CURRENT_NOTE)) {
            currentNote = intent.getParcelableExtra(CURRENT_NOTE)
        }
        currentNote?.apply {
            noteTitle.text = convertStringToEditable(title)
            noteBody.text = convertStringToEditable(body)
        }
        previous.setOnClickListener {
            uploadNote()
        }
        save.setOnClickListener {
            uploadNote()
        }
        openKeyboard()
        observeErrorMessage()
    }

    private fun observeErrorMessage() {
        noteUploadViewModel.errorMessage.observe(this) { errorMessage ->
            showMessage(errorMessage)
        }
    }

    override fun onPause() {
        hideKeyboard(currentFocus?.windowToken)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isKeyboardVisible) {
            openKeyboard()
        }
    }

    private fun openKeyboard() {
        noteTitle.postDelayed({
            noteTitle.requestFocus()
            noteTitle.openKeyboard { keyboardVisibility ->
                isKeyboardVisible = keyboardVisibility
            }
        }, 500)
    }

    private fun inputsFromUI(): Map<String, String> {
        val noteTitle = noteTitle.text.toString()
        val noteBody = noteBody.text.toString()
        return mutableMapOf(NOTE_TITLE to noteTitle, NOTE_BODY to noteBody)
    }

    private fun saveNote() {
        val inputs = inputsFromUI()
        if (isKeyboardVisible)
            hideKeyboard(currentFocus?.windowToken)
        noteUploadViewModel.uploadNote(
            inputs[NOTE_TITLE].toString(),
            inputs[NOTE_BODY].toString(),
            currentUser?.uid!!
        )
        finish()
    }

    private fun uploadNote() {
        if (currentNote == null)
            saveNote()
        else
            updateNote(currentNote!!)
    }

    private fun updateNote(currentNote: Note) {
        val inputs = inputsFromUI()
        if (currentNote.title != inputs[NOTE_TITLE].toString() || currentNote.body != inputs[NOTE_BODY].toString())
            noteUploadViewModel.updateNote(
                inputs[NOTE_TITLE].toString(),
                inputs[NOTE_BODY].toString(),
                currentNote
            )
        finish()
    }

    private fun convertStringToEditable(data: String) =
        Editable.Factory.getInstance().newEditable(data)
}