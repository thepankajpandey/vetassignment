package com.example.vetclinic.domain.repository

import com.example.vetclinic.data.model.ConfigDto
import com.example.vetclinic.data.model.PetsDto
import com.example.vetclinic.util.NetworkResult

interface VetRepository {
    suspend fun getConfig(): NetworkResult<ConfigDto>
    suspend fun getPets(): NetworkResult<PetsDto>
}