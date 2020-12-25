package com.app.takenote.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.app.takenote.R
import com.app.takenote.helper.ClickListener
import com.app.takenote.pojo.Note
import com.app.takenote.viewholder.NoteViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.lang.ref.WeakReference

class NoteAdapter(
    query: FirestoreRecyclerOptions<Note>,
    private val weakClickListener: WeakReference<ClickListener>
) :
    FirestoreRecyclerAdapter<Note, NoteViewHolder>(query), Filterable {
    private var notesArray: MutableList<Note> = mutableListOf()
    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val notesArray = snapshots.toArray()
            val filteredList = notesArray.filter { currentNote ->
                (currentNote as Note).title.contains(constraint ?: " ", true)
            }
            val filteredResults = FilterResults()
            filteredResults.values = filteredList
            return filteredResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notesArray.clear()
            results?.values?.let {
                notesArray.addAll(it as List<Note>)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false),
        weakClickListener
    )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Note) {
        if (notesArray.isNotEmpty()) {
            holder.bindView(notesArray[position])
        } else {
            holder.bindView(model)
        }
    }

    override fun getItemCount(): Int {
        if (notesArray.size != 0)
            return notesArray.size
        else
            return snapshots.size
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getFilter(): Filter {
        return filter
    }

    fun refresh() = notesArray.clear()
}