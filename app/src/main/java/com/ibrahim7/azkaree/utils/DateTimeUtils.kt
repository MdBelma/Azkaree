// DateTimeUtils.kt
package com.ibrahim7.azkaree.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private const val TIME_FORMAT_24_HOUR = "hh:mm" // 12-hour format with AM/PM

    fun getCurrentTime(locale: Locale = Locale("fr")): String {
        val timeFormat = SimpleDateFormat(TIME_FORMAT_24_HOUR, locale)
        return timeFormat.format(Calendar.getInstance().time)
    }
}