package com.app.takenote.extensions

import android.text.Editable
import android.widget.EditText

fun EditText.setData(value: String) {
    text = createEditable(value)
}

val EditText.data: String
    get() = text.toString().trim()

private fun createEditable(data: String) = Editable.Factory().newEditable(data)