package com.app.takenote.extensions

import java.util.*

val Calendar.year: Int
    get() = get(Calendar.YEAR)

val Calendar.month: Int
    get() = get(Calendar.MONTH)

val Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)

val Calendar.minute: Int
    get() = get(Calendar.MINUTE)

val Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)

fun Calendar.setCurrentTime(date: Date? = null) {
    date?.let {
        time = it
        return
    }
    time = Date()
}


