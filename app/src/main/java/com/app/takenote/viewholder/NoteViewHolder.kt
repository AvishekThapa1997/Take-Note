package com.app.takenote.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.takenote.extensions.*
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import com.app.takenote.utility.DateUtil
import com.app.takenote.utility.FORMATTED_DAY
import com.app.takenote.utility.FORMATTED_TIME
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
                DateUtil.calendar.setCurrentTime(Date(reminderTime))
                val formattedDate = cacheTime[currentNote.id] ?: DateUtil.formattedDate(
                    DateUtil.calendar.year,
                    DateUtil.calendar.dayOfMonth,
                    DateUtil.calendar.month,
                    DateUtil.calendar.hourOfDay,
                    DateUtil.calendar.minute
                )
                val dateToShow: String = if (formattedDate is Map<*, *>) {
                    val value = "${formattedDate[FORMATTED_DAY]},${formattedDate[FORMATTED_TIME]}"
                    cacheTime[currentNote.id] = value
                    value
                } else {
                    formattedDate.toString()
                }
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
    }
}