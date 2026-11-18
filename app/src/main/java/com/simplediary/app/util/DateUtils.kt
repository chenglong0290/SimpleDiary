package com.simplediary.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val zone: ZoneId = ZoneId.systemDefault()
    private val listFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy年M月d日", Locale.CHINA)

    fun epochDayOf(millis: Long = System.currentTimeMillis()): Long =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().toEpochDay()

    fun localDateOfEpochDay(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun formatListDate(epochDay: Long): String =
        localDateOfEpochDay(epochDay).format(listFormatter)

    fun today(): LocalDate = LocalDate.now(zone)
}
