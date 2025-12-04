package com.example.vetclinic.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.VetRepository
import com.example.vetclinic.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: VetRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init {
        reload()
    }

    fun reload() {
        _ui.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val c = repo.getConfig()) {
                is NetworkResult.Success -> {
                    _ui.update {
                        it.copy(
                            isChatEnabled = c.data.settings.isChatEnabled,
                            isCallEnabled = c.data.settings.isCallEnabled,
                            workHours = c.data.settings.workHours
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _ui.update { it.copy(error = "Config: ${c.message}") }
                }
            }
            when (val p = repo.getPets()) {
                is NetworkResult.Success -> _ui.update { it.copy(pets = p.data.pets, isLoading = false) }
                is NetworkResult.Error -> _ui.update { it.copy(error = "Pets: ${p.message}", isLoading = false) }
            }
        }
    }

    /**
     * Parse work hours like "M-F 9:00 - 18:00" or "Sat 10:00 - 14:00"
     * Returns true if current local time is inside.
     */
    fun isWithinWorkHours(): Boolean {
        val wh = _ui.value.workHours.ifBlank { return true }
        return try {
            val parts = wh.split(" ")
            if (parts.size < 4) return true
            val dayPart = parts[0]
            val startStr = parts[1]
            val endStr = parts[3]
            val now = LocalDateTime.now()
            val today = now.dayOfWeek

            fun parseDay(token: String): DayOfWeek =
                when (token.trim().uppercase(Locale.getDefault())) {
                    "M", "MONDAY", "MON" -> DayOfWeek.MONDAY
                    "T", "TUE", "TUESDAY" -> DayOfWeek.TUESDAY
                    "W", "WED", "WEDNESDAY" -> DayOfWeek.WEDNESDAY
                    "R", "THU", "THURSDAY" -> DayOfWeek.THURSDAY
                    "F", "FRI", "FRIDAY" -> DayOfWeek.FRIDAY
                    "SAT", "SATURDAY" -> DayOfWeek.SATURDAY
                    "SUN", "SUNDAY" -> DayOfWeek.SUNDAY
                    else -> DayOfWeek.MONDAY
                }

            val dayOk = if (dayPart.contains("-")) {
                val tokens = dayPart.split("-")
                val startD = parseDay(tokens[0])
                val endD = parseDay(tokens[1])
                val allowed = mutableSetOf<DayOfWeek>()
                var d = startD
                while (true) {
                    allowed.add(d)
                    if (d == endD) break
                    d = DayOfWeek.of((d.value % 7) + 1)
                }
                today in allowed
            } else {
                today == parseDay(dayPart)
            }

            if (!dayOk) return false

            val fmt = DateTimeFormatter.ofPattern("H:mm")
            val start = LocalTime.parse(startStr, fmt)
            val end = LocalTime.parse(endStr, fmt)
            val nowTime = now.toLocalTime()
            !nowTime.isBefore(start) && !nowTime.isAfter(end)
        } catch (_: Exception) {
            true
        }
    }
}