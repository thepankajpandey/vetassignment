package com.example.vetclinic.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.VetRepository
import com.example.vetclinic.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: VetRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init {
        loadData()
    }

    private fun loadData() {
        _ui.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val configResult = repo.getConfig()) {
                is NetworkResult.Success -> {
                    _ui.update {
                        it.copy(
                            isChatEnabled = configResult.data.settings.isChatEnabled,
                            isCallEnabled = configResult.data.settings.isCallEnabled,
                            workHours = configResult.data.settings.workHours
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _ui.update { it.copy(error = "Config: ${configResult.message}") }
                }
            }
            when (val p = repo.getPets()) {
                is NetworkResult.Success -> _ui.update {
                    it.copy(
                        pets = p.data.pets,
                        isLoading = false
                    )
                }

                is NetworkResult.Error -> _ui.update {
                    it.copy(
                        error = "Pets: ${p.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}