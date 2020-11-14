package com.app.takenote.helper


import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.takenote.R
import com.app.takenote.viewholder.NoteViewHolder
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class RecyclerViewSwipeCallBack : ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.START.or(ItemTouchHelper.END)
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (viewHolder as NoteViewHolder).deleteNote()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addActionIcon(R.drawable.delete)
            .addBackgroundColor(
                ContextCompat.getColor(
                    (viewHolder as NoteViewHolder).mView.context,
                    android.R.color.holo_red_light
                )
            )
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }
}