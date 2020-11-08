package com.app.takenote.helper

import com.app.takenote.pojo.Note

interface ClickListener {
    fun onClick(note : Note)
}