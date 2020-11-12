package com.app.takenote.pojo

import android.os.Parcel
import android.os.Parcelable
import com.app.takenote.utility.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    val title: String,
    val body: String,
    val authorId: String,
    val generatedDate: String
) : Parcelable {
    constructor() : this("", "", "", "", "")
//    constructor(source: Parcel) : this(
//        source.readString()!!,
//        source.readString()!!,
//        source.readString()!!,
//        source.readString()!!,
//        source.readString()!!
//    )
//
//    override fun describeContents() = 0
//
//    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
//        writeString(id)
//        writeString(title)
//        writeString(body)
//        writeString(authorId)
//        writeString(generatedDate)
//    }
//
//    companion object {
//        @JvmField
//        val CREATOR: Parcelable.Creator<Note> = object : Parcelable.Creator<Note> {
//            override fun createFromParcel(source: Parcel): Note = Note(source)
//            override fun newArray(size: Int): Array<Note?> = arrayOfNulls(size)
//        }
//    }

    fun toMap(): Map<String, String> {
        return mutableMapOf(
            NOTE_ID to id,
            NOTE_TITLE to title,
            NOTE_BODY to body,
            AUTHOR_ID to authorId,
            GENERATED_DATE to generatedDate
        )
    }
}