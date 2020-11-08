package com.app.takenote.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import kotlinx.android.synthetic.main.note_layout.view.*
import java.lang.ref.WeakReference

class NoteViewHolder(
    private val mView: View,
    private val weakReference: WeakReference<ClickListener>
) : RecyclerView.ViewHolder(mView) {

    fun bindView(currentNote: Note) {
        mView.title.text = currentNote.title
        mView.body.text = currentNote.body
        mView.setOnClickListener {
            weakReference.get()?.onClick(currentNote)
        }
    }
}