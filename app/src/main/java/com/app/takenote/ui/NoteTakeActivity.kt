package com.app.takenote.ui


import android.os.Bundle
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.utility.hideKeyboard
import com.app.takenote.utility.openKeyboard
import kotlinx.android.synthetic.main.activity_note_take.*


class NoteTakeActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_note_take
    override fun enabledFullScreen() = true
    private var isKeyboardVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previous.setOnClickListener {
            finish()
        }
        save.setOnClickListener {
            saveNote()
        }
        openKeyboard()
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

    private fun saveNote() {
        val noteTitle = noteTitle.text.toString()
        val noteBody = noteBody.text.toString()
        if (noteTitle.isEmptyOrIsBlank() || noteBody.isEmptyOrIsBlank() && isKeyboardVisible)
            hideKeyboard(currentFocus?.windowToken)
    }
}