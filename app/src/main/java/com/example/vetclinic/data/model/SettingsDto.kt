package com.example.vetclinic.data.model

data class SettingsDto(
    val isChatEnabled: Boolean,
    val isCallEnabled: Boolean,
    val workHours: String
)
