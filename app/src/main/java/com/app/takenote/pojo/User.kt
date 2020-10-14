package com.app.takenote.pojo

import android.os.Parcel
import android.os.Parcelable

data class User(
    val fullName: String?,
    val email: String?,
    val password: String?,
    val imageUri: String?
) : Parcelable {
    constructor(fullName: String, email: String) : this(fullName, email, "", "")

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(fullName)
        writeString(email)
        writeString(password)
        writeString(imageUri)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}