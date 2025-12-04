package com.example.vetclinic.domain.repository

import com.example.vetclinic.data.model.ConfigDto
import com.example.vetclinic.data.model.PetsDto
import com.example.vetclinic.data.remote.HttpApiService
import com.example.vetclinic.util.NetworkResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VetRepositoryImpl @Inject constructor(
    private val api: HttpApiService
): VetRepository {
    override suspend fun getConfig(): NetworkResult<ConfigDto> = api.fetchConfig()
    override suspend fun getPets(): NetworkResult<PetsDto> = api.fetchPets()
}