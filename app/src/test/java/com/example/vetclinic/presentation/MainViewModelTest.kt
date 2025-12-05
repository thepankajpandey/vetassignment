package com.example.vetclinic.presentation

import com.example.vetclinic.MainDispatcherRule
import com.example.vetclinic.data.model.ConfigDto
import com.example.vetclinic.data.model.PetDto
import com.example.vetclinic.data.model.PetsDto
import com.example.vetclinic.data.model.SettingsDto
import com.example.vetclinic.domain.repository.VetRepository
import com.example.vetclinic.presentation.main.MainViewModel
import com.example.vetclinic.util.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    private lateinit var repo: VetRepository
    private lateinit var vm: MainViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val config = ConfigDto(
        settings = SettingsDto(
            isChatEnabled = true,
            isCallEnabled = false,
            workHours = "M-F 9:00 - 18:00"
        )
    )

    private val pets = PetsDto(
        pets = listOf(
            PetDto(
                title = "Dog",
                image_url = "http://example.com/dog.png",
                content_url = "http://example.com",
                date_added = "2023-01-01"
            )
        )
    )

    @Before
    fun setup() {
        repo = mockk()
    }

    @Test
    fun `loadData loads config and pets successfully`() = runTest {

        coEvery { repo.getConfig() } returns NetworkResult.Success(config, 200)
        coEvery { repo.getPets() } returns NetworkResult.Success(pets, 200)

        vm = MainViewModel(repo)

        val state = vm.ui.value

        assertEquals(false, state.isLoading)
        assertEquals(true, state.isChatEnabled)
        assertEquals(false, state.isCallEnabled)
        assertEquals("M-F 9:00 - 18:00", state.workHours)
        assertEquals(1, state.pets.size)
    }

    @Test
    fun `loadData returns config error`() = runTest {

        coEvery { repo.getConfig() } returns NetworkResult.Error(404, "Not Found")
        coEvery { repo.getPets() } returns NetworkResult.Success(pets, 200)

        vm = MainViewModel(repo)

        val state = vm.ui.value

        assertEquals("Config: Not Found", state.error)
    }

    @Test
    fun `loadData returns pets error`() = runTest {

        coEvery { repo.getConfig() } returns NetworkResult.Success(config, 200)
        coEvery { repo.getPets() } returns NetworkResult.Error(500, "Server Error")

        vm = MainViewModel(repo)

        val state = vm.ui.value

        assertEquals("Pets: Server Error", state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `state starts with loading true`() = runTest {

        coEvery { repo.getConfig() } returns NetworkResult.Success(config, 200)
        coEvery { repo.getPets() } returns NetworkResult.Success(pets, 200)

        vm = MainViewModel(repo)

        val state = vm.ui.value

        assertFalse(state.isLoading)
    }
}