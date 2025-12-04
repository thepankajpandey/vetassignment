package com.example.vetclinic.di

import com.example.vetclinic.data.remote.HttpApiService
import com.example.vetclinic.domain.repository.VetRepository
import com.example.vetclinic.domain.repository.VetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoBindModule {
    @Binds
    abstract fun bindVetRepository(impl: VetRepositoryImpl): VetRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val CONFIG_URL = "Some resource url"
    private const val PETS_URL = "Some resource url"

    @Provides
    @Singleton
    fun provideHttpApiService(): HttpApiService = HttpApiService(CONFIG_URL, PETS_URL)

    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}