package com.example.vetclinic.presentation.main

import com.example.vetclinic.data.model.PetDto

data class UiState(
    val isLoading: Boolean = true,
    val isChatEnabled: Boolean = false,
    val isCallEnabled: Boolean = false,
    val workHours: String = "",
    val pets: List<PetDto> = emptyList(),
    val error: String? = null
)
