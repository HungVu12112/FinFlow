package com.devux.finflow.utils

import java.util.Calendar

object TimeUtils {
    fun getStartAndEndOfDay(timestamp: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    fun getStartAndEndOfMonth(timestamp: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

    fun getStartAndEndOfYear(timestamp: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        calendar.set(Calendar.DAY_OF_YEAR, 1) // Ngày đầu năm
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.MONTH, 11) // Tháng 12
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }
}