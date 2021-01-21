package com.app.takenote.utility

import java.text.DateFormatSymbols
import java.util.Date
import java.util.Calendar

object DateUtil {
    val calendar: Calendar by lazy {
        Calendar.getInstance()
    }
    private val months: Array<String> by lazy {
        DateFormatSymbols().months
    }
    val currentTime: Long = Date().time
    fun getMonthName(index: Int) = months[index]
    fun formattedValue(value1: Int, value2: Int, separator: String): String {
        val formattedFirstValue = if (value1.toString().length < 2)
            "0${value1}"
        else
            "$value1"
        val formattedSecondValue = if (value2.toString().length < 2)
            "0${value2}"
        else
            "$value2"
        return "$formattedFirstValue$separator$formattedSecondValue"
    }

    fun formattedDate(
        year: Int,
        dayOfMonth: Int,
        month: Int,
        hour: Int,
        minute: Int
    ): MutableMap<String, String> {
        val formattedDay = if ("$dayOfMonth".length < 2) {
            "0$dayOfMonth ${months[month]} $year"
        } else {
            "$dayOfMonth ${months[month]} $year"
        }
        val formattedTime = when (hour) {
            0 -> {
                "12:${minute} $AM"
            }
            in 1..11 -> {
                "$hour:${minute} $AM"
            }
            else -> {
                val time = if (hour - 12 != 0)
                    hour - 12
                else
                    12
                "$time:${minute} $PM"
            }
        }
        return mutableMapOf(FORMATTED_DAY to formattedDay, FORMATTED_TIME to formattedTime)
    }
}

