package com.app.takenote.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.takenote.R
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import com.app.takenote.viewholder.NoteViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.lang.ref.WeakReference

class NoteAdapter(
    query: FirestoreRecyclerOptions<Note>,
   private val weakClickListener : WeakReference<ClickListener>
) :
    FirestoreRecyclerAdapter<Note, NoteViewHolder>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false),weakClickListener
    )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Note) {
        holder.bindView(model)
    }

    override fun getItemId(position: Int) = position.toLong()

}