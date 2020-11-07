package com.app.takenote.utility

import `in`.codeshuffle.typewriterview.TypeWriterListener

interface CustomTypeWriterListener : TypeWriterListener {
    override fun onCharacterTyped(text: String?, position: Int){
    }

    override fun onTypingRemoved(text: String?) {

    }
    override fun onTypingEnd(text: String?) {

    }

    override fun onTypingStart(text: String?) {

    }
}