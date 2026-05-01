package com.rambo.ramcryptr

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DateUtils {

    // Base date (fixed reference point)
    private val BASE_DATE: LocalDate = LocalDate.of(2025, 1, 1)

    fun getDayIndex(): Int {
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(BASE_DATE, today).toInt()
    }

    fun getDayIndexFrom(date: LocalDate): Int {
        return ChronoUnit.DAYS.between(BASE_DATE, date).toInt()
    }
}
