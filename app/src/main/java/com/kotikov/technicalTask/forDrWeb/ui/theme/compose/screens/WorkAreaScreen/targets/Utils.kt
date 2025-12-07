package com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.WorkAreaScreen.targets

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun convertMillisToTime(milliseconds: Long): String {
    val date = Date(milliseconds)
    val format = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return format.format(date)
}

fun convertMillisToDateTime(milliseconds: Long): String {
    if (milliseconds == 0L) return "нет данных"
    val date = Date(milliseconds)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return format.format(date)
}
