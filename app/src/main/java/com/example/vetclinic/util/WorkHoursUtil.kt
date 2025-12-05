package com.example.vetclinic.util

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

object WorkHoursUtil {

    fun isWithinWorkHours(workHours: String): Boolean {
        val parts = workHours.split(" ")
        if (parts.size < 3) return false

        val dayRange = parts[0]
        val startTimeStr = parts[1]
        val endTimeStr = parts[3]

        val dayParts = dayRange.split("-")
        if (dayParts.size != 2) return false

        val startDay = parseDayLetter(dayParts[0])
        val endDay = parseDayLetter(dayParts[1])
        if (startDay == null || endDay == null) return false

        val now = LocalDateTime.now()
        val today = now.dayOfWeek

        if (today.value < startDay.value || today.value > endDay.value) {
            return false
        }

        val startTime = LocalTime.parse(startTimeStr)
        val endTime = LocalTime.parse(endTimeStr)

        val nowTime = now.toLocalTime()

        return nowTime.isAfter(startTime) && nowTime.isBefore(endTime)
    }

    private fun parseDayLetter(letter: String): DayOfWeek? {
        return when (letter.uppercase()) {
            "M" -> DayOfWeek.MONDAY
            "T" -> DayOfWeek.TUESDAY
            "W" -> DayOfWeek.WEDNESDAY
            "TH" -> DayOfWeek.THURSDAY
            "F" -> DayOfWeek.FRIDAY
            "SA" -> DayOfWeek.SATURDAY
            "SU" -> DayOfWeek.SUNDAY
            else -> null
        }
    }
}