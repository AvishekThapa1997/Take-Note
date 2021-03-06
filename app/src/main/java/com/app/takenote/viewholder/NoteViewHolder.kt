package com.app.takenote.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.app.takenote.extensions.*
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import com.app.takenote.utility.DateUtil
import com.app.takenote.utility.FORMATTED_DAY
import com.app.takenote.utility.FORMATTED_TIME
import com.app.takenote.utility.TIME_MERIDIAN
import kotlinx.android.synthetic.main.note_layout.view.*
import kotlinx.android.synthetic.main.reminder_layout.view.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.Delegates

class NoteViewHolder(
    val mView: View,
    private val weakReference: WeakReference<ClickListener>
) : RecyclerView.ViewHolder(mView) {

    private val cacheTime: MutableMap<String, String> = mutableMapOf()
    private var note: Note? = null
    fun bindView(currentNote: Note) {
        note = currentNote
        mView.setOnClickListener {
            weakReference.get()?.onClick(currentNote)
        }
        mView.title.text = currentNote.title
        mView.body.text = currentNote.body
        if (!currentNote.reminderTime.isEmptyOrIsBlank()) {
            val reminderTime = currentNote.reminderTime.toLongOrNull() ?: 0
            if (reminderTime > DateUtil.currentTime) {
                val calendar = Calendar.getInstance()
                calendar.time = Date(reminderTime)
                val formattedDate = DateUtil.formattedDate(
                    calendar.year,
                    calendar.dayOfMonth,
                    calendar.month,
                    calendar.hourOfDay,
                    calendar.minute
                )
//                val dateToShow: String = if (formattedDate is Map<*, *>) {
//                    val value = "${formattedDate[FORMATTED_DAY]},${
//                        formattedDate[FORMATTED_TIME].toString()
//                            .plus(" ${formattedDate[TIME_MERIDIAN]}")
//                    }"
//                    cacheTime[currentNote.id] = value
//                    value
//                } else {
//                    formattedDate.toString()
//                }
                val dateToShow = "${formattedDate[FORMATTED_DAY]},${
                    formattedDate[FORMATTED_TIME].toString()
                        .plus(" ${formattedDate[TIME_MERIDIAN]}")
                }"
                mView.tvSelectedDate.text = dateToShow
                mView.flowReminderContainer.visibility = View.VISIBLE
            } else {
                mView.flowReminderContainer.visibility = View.GONE
            }
        } else {
            mView.flowReminderContainer.visibility = View.GONE
        }
    }

    fun deleteNote() {
        weakReference.get()?.deleteNote(adapterPosition)
        note?.let {
            cacheTime.remove(it.id)
        }
        note?.let {
            val workManager = WorkManager.getInstance(mView.context)
            workManager.cancelUniqueWork(it.id)
        }
    }
}