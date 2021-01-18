package com.app.takenote.extensions

import android.text.Editable
import android.widget.EditText

fun EditText.setData(value: String) {
    text = createEditable(value)
}

private fun createEditable(data: String) = Editable.Factory().newEditable(data)