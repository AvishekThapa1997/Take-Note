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
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_note_take.*
import kotlinx.android.synthetic.main.reminder_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


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
    private var realTimeListener: ListenerRegistration? = null
    private var timeMeridian: String = ""
    private val formattedDateAndTime: MutableMap<String, String> by lazy {
        mutableMapOf()
    }
    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener {
            uploadNote()
        }
        val bundle = intent.getBundleExtra(BUNDLE)
        currentUser = bundle?.getParcelable(CURRENT_USER)
        if (bundle?.containsKey(CURRENT_NOTE) == true) {
            currentNote = bundle.getParcelable(CURRENT_NOTE)
        }
        currentNote?.apply {
            noteTitle.text = convertStringToEditable(title)
            noteBody.text = convertStringToEditable(body)
            onBackground {
                if (!reminderTime.isEmptyOrIsBlank())
                    setTime(reminderTime)
            }
        }
        openKeyboard()
        observeErrorMessage()
        observeNoteId()
    }

    private fun observeNoteId() {
        onBackground {
            noteUploadViewModel.noteId.collect { note ->
                val time = note.reminderTime
                if (!time.isEmptyOrIsBlank())
                    setReminderWorker(
                        note.title,
                        note.body,
                        (time.toLong() - DateUtil.currentTime),
                        note.id
                    )
            }

        }
    }

    private suspend fun setTime(reminderTime: String) {
        val time = reminderTime.toLongOrNull() ?: 0
        if (time > DateUtil.currentTime) {
            val calendar = Calendar.getInstance()//DateUtil.calendar.setCurrentTime(Date(time))
            calendar.time = Date(time)
            val formattedDate = DateUtil.formattedDate(
                calendar.year,
                calendar.dayOfMonth,
                calendar.month,
                calendar.hourOfDay,
                calendar.minute
            )
            val formattedDay: String = formattedDate[FORMATTED_DAY]!!
            val formattedTime: String = formattedDate[FORMATTED_TIME]!!
            val timeMeridian: String = formattedDate[TIME_MERIDIAN]!!
            val dateToShow =
                "${formattedDay}," + formattedTime.plus(" $timeMeridian")
            //dayToShow = formattedDay.substring(0, formattedDay.lastIndexOf(" "))
            dayToShow = formattedDay
            timeToShow = formattedTime.plus(" ${formattedDate[TIME_MERIDIAN]}")
            daySelected = formattedDate[FORMATTED_DATE]!!
            timeSelected = formattedDate[FORMATTED_TIME]!!
            formattedDateAndTime[FORMATTED_DAY] = dayToShow
            formattedDateAndTime[FORMATTED_TIME] = timeToShow
            formattedDateAndTime[TIME_MERIDIAN] = timeMeridian
            selectedDate = Date(time)
            withContext(Dispatchers.Main) {
                tvSelectedDate.text = dateToShow
                flowReminderContainer.visibility = View.VISIBLE
            }
        } else {
            withContext(Dispatchers.Main) {
                flowReminderContainer.visibility = View.GONE
            }
        }
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


    override fun onResume() {
        super.onResume()
        if (isKeyboardVisible) {
            openKeyboard()
        }
        currentNote?.let { note ->
            realTimeListener = fireStore.collection(NOTE_COLLECTION).document(note.id)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, _ ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val updatedTime = documentSnapshot[REMINDER_TIME].toString()
                        onBackground {
                            if (!updatedTime.isEmptyOrIsBlank() &&
                                updatedTime != currentNote?.reminderTime
                            )
                                setTime(updatedTime)
                        }
                    }
                }

        }
    }

    override fun onPause() {
        hideKeyboard(currentFocus?.windowToken)
        super.onPause()
        realTimeListener?.remove()
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
        val noteTitle = noteTitle.data
        val noteBody = noteBody.data
        return mutableMapOf(NOTE_TITLE to noteTitle, NOTE_BODY to noteBody)
    }

    private fun saveNote() {
        val inputs = inputsFromUI()
        if (isKeyboardVisible) {
            hideKeyboard(currentFocus?.windowToken)
            isKeyboardVisible = false
        }
        val reminderTime = "${selectedDate?.time ?: ""}"
        noteUploadViewModel.uploadNote(
            inputs[NOTE_TITLE].toString(),
            inputs[NOTE_BODY].toString(),
            currentUser?.uid!!,
            reminderTime
        )
//        if (!reminderTime.isEmptyOrIsBlank())
//            setReminderWorker(
//                inputs[NOTE_TITLE].toString(),
//                reminderTime.toLong() - DateUtil.currentTime,
//                currentNote?.id!!
//            )
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
        val updatedData = mutableMapOf<String, String>()
        val title: String = inputs[NOTE_TITLE] ?: ""
        val body = inputs[NOTE_BODY] ?: ""
        if (currentNote.title != title)
            updatedData[NOTE_TITLE] = title
        if (currentNote.body != body)
            updatedData[NOTE_BODY] = body
        val time = "${selectedDate?.time ?: ""}"
        if (!time.isEmptyOrIsBlank() && time != currentNote.reminderTime)
            updatedData[REMINDER_TIME] = time
        if (updatedData.isNotEmpty()) {
            noteUploadViewModel.updateNote(
                updatedData,
                currentNote
            )
            if (!time.isEmptyOrIsBlank()) {
                val timeToSet = time.toLong() - DateUtil.currentTime
                setReminderWorker(title, body, timeToSet, currentNote.id)
            }
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
        var btnDelete: TextView? = null
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        if (formattedDateAndTime.isNotEmpty()) {
            btnDelete = dialogView.extractView(R.id.tvDelete)
            val formattedDay = formattedDateAndTime[FORMATTED_DAY]!!
            btnShowDatePicker.setData(formattedDay.substring(0, formattedDay.lastIndexOf(" ")))
            btnShowTimePicker.setData(formattedDateAndTime[FORMATTED_TIME]!!)
            btnDelete?.visibility = View.VISIBLE
        }
        btnDelete?.setOnClickListener {
            noteUploadViewModel.deleteTime(currentNote!!)
            refreshDateData()
            flowReminderContainer.visibility = View.GONE
            currentNote = currentNote?.copy(reminderTime = "")
            alertDialog.cancel()
        }
        alertDialog.show()
        btnCancel.setOnClickListener {
            selectedDate?.let {
                DateUtil.calendar.time = it
            }
            currentNote?.id?.let {
                workManager.cancelUniqueWork(it)
            }
            alertDialog.cancel()
        }
        btnShowDatePicker.setOnClickListener {
            //DateUtil.calendar.setCurrentTime()
            if (isKeyboardVisible) {
                hideKeyboard(currentFocus?.windowToken)
                showDatePicker(it as EditText)
            }
        }
        btnShowTimePicker.setOnClickListener {
            //DateUtil.calendar.setCurrentTime()
            if (isKeyboardVisible) {
                hideKeyboard(currentFocus?.windowToken)
                showTimePicker(it as EditText)
            }
        }
        btnApply.setOnClickListener {
            if (!daySelected.isEmptyOrIsBlank() && !timeSelected.isEmptyOrIsBlank()) {
                val dateInString = daySelected.plus(":$timeSelected")
                selectedDate = DateUtil.createDate(dateInString)
                selectedDate?.let { _selectedDate ->
                    if (DateUtil.currentTime > _selectedDate.time) {
                        Toast.makeText(this, "Invalid Time", Toast.LENGTH_SHORT).show()
                        selectedDate = null
                        return@setOnClickListener
                    } else {
                        tvSelectedDate.text = dayToShow.plus(",").plus(timeToShow)
                        formattedDateAndTime[FORMATTED_DAY] = dayToShow
                        formattedDateAndTime[FORMATTED_TIME] = timeToShow
                        flowReminderContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                Snackbar.make(rootView, "Time Discarded", Snackbar.LENGTH_SHORT).show()
            }
            alertDialog.cancel()
        }
    }

    private fun refreshDateData() {
        selectedDate = null
        dayToShow = ""
        daySelected = ""
        timeSelected = ""
        timeToShow = ""
        if (formattedDateAndTime.isNotEmpty())
            formattedDateAndTime.clear()
    }

    private fun showDatePicker(editText: EditText) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                daySelected = "${year}-${formattedValue(month + 1, dayOfMonth, "-")}"
                dayToShow = "$dayOfMonth ${getMonthName(month)} $year"
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
                        val formattedTime = formattedValue(0, minute, ":")
                        timeMeridian = AM
                        timeToShow = "12:${formattedTime.separateMinute()}".plus(" $timeMeridian")
                        editText.setData(timeToShow)
                        formattedTime
                    }
                    in 1..11 -> {
                        val formattedTime = formattedValue(hourOfDay, minute, ":")
                        timeMeridian = AM
                        timeToShow = formattedTime.plus(" $timeMeridian")
                        editText.setData(formattedTime.plus(" $timeMeridian"))
                        formattedTime
                    }
                    else -> {
                        val time = if (hourOfDay - 12 != 0)
                            "0${hourOfDay - 12}"
                        else
                            12
                        val formattedTime = formattedValue(hourOfDay, minute, ":")
                        timeMeridian = PM
                        timeToShow =
                            "$time:${formattedTime.separateMinute()}".plus(" $timeMeridian")
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

    private fun setReminderWorker(noteTitle: String, noteBody: String, time: Long, noteId: String) {
        workManager.apply {
            val workRequest = setReminderWorker(noteTitle, noteBody, noteId, time)
            enqueueUniqueWork(
                noteId,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}