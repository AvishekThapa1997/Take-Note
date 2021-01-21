package com.app.takenote.ui


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.takenote.R
import com.app.takenote.extensions.*
import com.app.takenote.pojo.Note
import com.app.takenote.pojo.User
import com.app.takenote.utility.*
import com.app.takenote.utility.DateUtil
import com.app.takenote.utility.DateUtil.formattedValue
import com.app.takenote.utility.DateUtil.getMonthName
import com.app.takenote.viewmodels.NoteUploadViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_note_take.*
import kotlinx.android.synthetic.main.reminder_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class NoteUploadActivity : BaseActivity() {
    override val layoutResourceId = R.layout.activity_note_take
    override fun enabledFullScreen() = true
    private var isKeyboardVisible = false
    private val noteUploadViewModel: NoteUploadViewModel by viewModel()
    private var currentUser: User? = null
    private var currentNote: Note? = null
    private var daySelected: String = ""
    private var timeSelected: String = ""
    private var selectedDate: Date? = null
    private var dayToShow: String = ""
    private var timeToShow: String = ""
    private var formattedDateAndTime: MutableMap<String, String>? = null
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
            onBackground {
                Log.i("TAG", "onCreate: ${Thread.currentThread().name}")
                if (!reminderTime.isEmptyOrIsBlank()) {
                    val time = reminderTime.toLongOrNull() ?: 0
                    if (time > DateUtil.currentTime) {
                        DateUtil.calendar.setCurrentTime(Date(time))
                        val formattedDate = DateUtil.formattedDate(
                            DateUtil.calendar.year,
                            DateUtil.calendar.dayOfMonth,
                            DateUtil.calendar.month,
                            DateUtil.calendar.hourOfDay,
                            DateUtil.calendar.minute
                        )
                        formattedDateAndTime = formattedDate
                        val dateToShow = "${formattedDate[FORMATTED_DAY]}," +
                                "${formattedDate[FORMATTED_TIME]}"
                        withContext(Dispatchers.Main) {
                            tvSelectedDate.text = dateToShow
                            flowReminderContainer.visibility = View.VISIBLE
                        }
                    }
                }
            }
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
            currentUser?.uid!!,
            "${selectedDate?.time ?: ""}"
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
        val btnShowTimePicker = dialogView.extractView<EditText>(R.id.selectTime)
        val btnCancel = dialogView.extractView<TextView>(R.id.tvCancel)
        val btnApply = dialogView.extractView<Button>(R.id.btnSave)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        formattedDateAndTime?.apply {
            if (isNotEmpty()) {
                btnShowDatePicker.setData(get(FORMATTED_DAY) ?: "Select Date")
                btnShowTimePicker.setData(get(FORMATTED_TIME) ?: "Select Time")
            }
        }
        alertDialog.show()
        btnCancel.setOnClickListener {
            alertDialog.cancel()
        }
        btnShowDatePicker.setOnClickListener {
            DateUtil.calendar.setCurrentTime(Date())
            if (isKeyboardVisible) {
                hideKeyboard(currentFocus?.windowToken)
                showDatePicker(it as EditText)
            }
        }
        btnShowTimePicker.setOnClickListener {
            DateUtil.calendar.setCurrentTime(Date())
            if (isKeyboardVisible) {
                hideKeyboard(currentFocus?.windowToken)
                showTimePicker(it as EditText)
            }
        }
        btnApply.setOnClickListener {
            if (!daySelected.isEmptyOrIsBlank() && !timeSelected.isEmptyOrIsBlank()) {
                val dateInString = daySelected.plus(":$timeSelected")
                selectedDate = SimpleDateFormat(
                    "yyyy-MM-dd:HH:mm",
                    Locale.getDefault()
                ).parse(dateInString.substring(0, dateInString.indexOf(" ")))
                selectedDate?.let { _selectedDate ->
                    if (DateUtil.currentTime > _selectedDate.time) {
                        Toast.makeText(this, "Invalid Time", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        tvSelectedDate.text = dayToShow.plus(",").plus(timeToShow)
                        flowReminderContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                Snackbar.make(rootView, "Time Discarded", Snackbar.LENGTH_SHORT).show()
            }
            alertDialog.cancel()
        }
    }

    private fun showDatePicker(editText: EditText) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                daySelected = "${year}-${formattedValue(month + 1, dayOfMonth, "-")}"
                dayToShow = "$dayOfMonth ${getMonthName(month)}"
                editText.setData(dayToShow)
            },
            DateUtil.calendar.year,
            DateUtil.calendar.month,
            DateUtil.calendar.dayOfMonth
        )
        datePickerDialog.datePicker.minDate = DateUtil.currentTime
        datePickerDialog.show()
    }

    private fun showTimePicker(editText: EditText) {
        val currentHour = DateUtil.calendar.hourOfDay
        val currentMinute = DateUtil.calendar.minute
        val timePickerDialog = TimePickerDialog(
            this, { _, hourOfDay, minute ->
                timeSelected = when (hourOfDay) {
                    0 -> {
                        val formattedTime = formattedValue(0, minute, ":").plus(" $AM")
                        timeToShow = "12:${
                            formattedTime.substring(
                                formattedTime.indexOf(":") + 1
                            )
                        }"
                        editText.setData(timeToShow)
                        formattedTime
                    }
                    in 1..11 -> {
                        val formattedTime = formattedValue(hourOfDay, minute, ":").plus(" $AM")
                        timeToShow = formattedTime
                        editText.setData(formattedTime)
                        formattedTime
                    }
                    else -> {
                        val time = if (hourOfDay - 12 != 0)
                            hourOfDay - 12
                        else
                            12
                        val formattedTime = formattedValue(hourOfDay, minute, ":").plus(" $PM")
                        timeToShow = "$time:${
                            formattedTime.substring(
                                formattedTime.indexOf(":") + 1
                            )
                        }"
                        editText.setData(timeToShow)
                        formattedTime
                    }
                }
            },
            currentHour,
            currentMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun <T : View> View.extractView(viewId: Int) = findViewById<T>(viewId)


}