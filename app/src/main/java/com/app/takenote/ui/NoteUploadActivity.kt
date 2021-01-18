package com.app.takenote.ui


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.takenote.R
import com.app.takenote.extensions.setData
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.NoteUploadViewModel
import kotlinx.android.synthetic.main.activity_note_take.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DateFormatSymbols
import java.util.*


class NoteUploadActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_note_take
    override fun enabledFullScreen() = true
    private var isKeyboardVisible = false
    private val noteUploadViewModel: NoteUploadViewModel by viewModel()
    private var currentUser: User? = null
    private var currentNote: Note? = null

    private val calendar: Calendar by lazy {
        Calendar.getInstance()
    }
    private val months: Array<String> by lazy {
        DateFormatSymbols().months
    }
    private val todayDate: Date by lazy {
        Date()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        if (bundle?.containsKey(CURRENT_NOTE) == true) {
            currentNote = bundle.getParcelable(CURRENT_NOTE)
        }
        currentNote?.apply {
            noteTitle.text = convertStringToEditable(title)
            noteBody.text = convertStringToEditable(body)
        }
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener {
            uploadNote()
        }
        openKeyboard()
        observeErrorMessage()
    }

    private fun observeErrorMessage() {
        noteUploadViewModel.message.observe(this) { message ->
            if (message == NOTE_DISCARDED) {
                setResultForPreviousActivity(message)
            }
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuId = item.itemId
        if (menuId == R.id.save)
            uploadNote()
        else {
            showAddReminderDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setResultForPreviousActivity(message: String) {
        val intent = Intent()
        intent.putExtra(MESSAGE, message)
        setResult(RESULT_CODE, intent)
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
        if (isKeyboardVisible) {
            hideKeyboard(currentFocus?.windowToken)
            isKeyboardVisible = false
        }
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
        else {
            setResultForPreviousActivity(NOTE_DISCARDED)
        }
        finish()
    }

    private fun convertStringToEditable(data: String) =
        Editable.Factory.getInstance().newEditable(data)

    private fun showAddReminderDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.reminder_dialog, rootView, false)
        val btnShowDatePicker = dialogView.extractView<EditText>(R.id.selectDate)
        val selectTime = dialogView.extractView<EditText>(R.id.selectTime)
        val btnCancel = dialogView.extractView<TextView>(R.id.tvCancel)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        btnCancel.setOnClickListener {
            alertDialog.cancel()
        }
        btnShowDatePicker.setOnClickListener {
            if (isKeyboardVisible) {
                hideKeyboard(currentFocus?.windowToken)
                showDatePicker(it as EditText)
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, _, month, dayOfMonth ->
                editText.setData("$dayOfMonth ${months[month]}")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONDAY),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = todayDate.time
        datePickerDialog.show()
    }

    private fun <T : View> View.extractView(viewId: Int) = findViewById<T>(viewId)
}