package com.euzhene.comranet.util

import java.text.SimpleDateFormat
import java.util.*

const val H_MM = "h:mm a"
const val D_MMM_YYYY = "d MMM yyyy"

fun mapTimestampToDate(timestamp: Long, pattern: String): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}

fun datesEqual(timestamp1: Long, timestamp2: Long, pattern: Int): Boolean {
    val calendar1 = Calendar.getInstance().apply { time = Date(timestamp1) }
    val calendar2 = Calendar.getInstance().apply { time = Date(timestamp2) }
    return calendar1.get(pattern) == calendar2.get(pattern)
}
