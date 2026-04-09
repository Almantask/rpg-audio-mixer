package com.example.rpgaudiomixer.app.ui.library

import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SoundscapeLibraryViewModelTest {

    private val repository = mockk<SoundscapeRepository>(relaxed = true)
    private lateinit var viewModel: SoundscapeLibraryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state eventually becomes Success empty`() = runTest {
        val state = viewModel.uiState.first { it is SoundscapeLibraryUiState.Success }
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
        assertThat((state as SoundscapeLibraryUiState.Success).categories).isEmpty()
    }
}
