package com.example.vetclinic.util

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WorkHoursUtilTest {

    @Before
    fun setup() {
        mockkStatic(LocalDateTime::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun mockNow(day: DayOfWeek, hour: Int, minute: Int = 0) {
        val date = LocalDate.of(2023, 1, 1)
            .with(day)
        val time = LocalTime.of(hour, minute)
        val dateTime = LocalDateTime.of(date, time)
        every { LocalDateTime.now() } returns dateTime
    }

    @Test
    fun `returns true when inside work hours`() {
        mockNow(DayOfWeek.WEDNESDAY, 10, 0)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00 - 18:00")

        assertTrue(result)
    }

    @Test
    fun `returns false when before work hours`() {
        mockNow(DayOfWeek.TUESDAY, 7)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00 - 18:00")

        assertFalse(result)
    }

    @Test
    fun `returns false when after work hours`() {
        mockNow(DayOfWeek.THURSDAY, 20)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00 - 18:00")

        assertFalse(result)
    }

    @Test
    fun `returns false when on weekend but range is weekdays`() {
        mockNow(DayOfWeek.SATURDAY, 12)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00 - 18:00")

        assertFalse(result)
    }

    @Test
    fun `returns true when on Friday within range`() {
        mockNow(DayOfWeek.FRIDAY, 15)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00 - 18:00")

        assertTrue(result)
    }

    @Test
    fun `returns false for malformed work hours string`() {
        mockNow(DayOfWeek.MONDAY, 10)

        val result = WorkHoursUtil.isWithinWorkHours("INVALID STRING")

        assertFalse(result)
    }

    @Test
    fun `returns false for malformed day range`() {
        mockNow(DayOfWeek.MONDAY, 10)

        val result = WorkHoursUtil.isWithinWorkHours("X-Z 09:00 - 18:00")

        assertFalse(result)
    }

    @Test
    fun `returns false when parts size is incorrect`() {
        mockNow(DayOfWeek.MONDAY, 10)

        val result = WorkHoursUtil.isWithinWorkHours("M-F 09:00")

        assertFalse(result)
    }

}
