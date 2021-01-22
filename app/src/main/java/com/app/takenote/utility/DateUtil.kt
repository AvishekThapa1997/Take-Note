package com.app.takenote.utility

import android.util.Log
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

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
        val timeMeridian: String
        val formattedDay = if ("$dayOfMonth".length < 2) {
            "0$dayOfMonth"
        } else {
            "$dayOfMonth"
        }
        val formattedMinute = if ("$minute".length < 2) {
            "0$minute"
        } else {
            "$minute"
        }
        val formattedHour = if ("$hour".length < 2) {
            "0$hour"
        } else {
            "$hour"
        }
        val formattedTime = when (hour) {
            0 -> {
                timeMeridian = AM
                "12:${formattedMinute}"
            }
            in 1..11 -> {
                timeMeridian = AM
                "$formattedHour:${formattedMinute}"
            }
            else -> {
                val time = if (hour - 12 != 0)
                    "0${hour - 12}"
                else
                    "12"
                timeMeridian = PM
                "$time:${formattedMinute}"
            }
        }
        val formattedMonth = if ("$month".length < 2)
            "0${month + 1}"
        else
            "${month + 1}"
        val formattedDate = "${year}-${formattedMonth}-${formattedDay}"
        return mutableMapOf(
            FORMATTED_DAY to formattedDay.plus(" ${months[month]} $year"),
            FORMATTED_TIME to formattedTime,
            FORMATTED_DATE to formattedDate,
            TIME_MERIDIAN to timeMeridian
        )
    }

    fun createDate(value: String): Date? = SimpleDateFormat(
        "yyyy-MM-dd:HH:mm",
        Locale.getDefault()
    ).parse(value)
}

