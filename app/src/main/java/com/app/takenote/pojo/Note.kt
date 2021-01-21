package com.app.takenote.pojo

import android.os.Parcelable
import com.app.takenote.utility.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    val title: String,
    val body: String,
    val authorId: String,
    val generatedDate: String,
    val reminderTime: String,
) : Parcelable {
    constructor() : this("", "", "", "", "", "")

    fun toMap(): Map<String, String> {
        return mutableMapOf(
            NOTE_ID to id,
            NOTE_TITLE to title,
            NOTE_BODY to body,
            AUTHOR_ID to authorId,
            GENERATED_DATE to generatedDate,
            REMINDER_TIME to reminderTime
        )
    }
}