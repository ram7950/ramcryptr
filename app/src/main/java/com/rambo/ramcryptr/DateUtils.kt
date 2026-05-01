package com.rambo.ramcryptr

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DateUtils {
    private val baseDate = LocalDate.of(2025, 1, 1)

    fun getDayIndex(): Int {
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(baseDate, today).toInt()
    }
}
