package com.example.rpgaudiomixer.app.ui.library

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import io.mockk.coEvery
import io.mockk.coVerify
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
class SoundscapeCategoryComposerViewModelTest {

    private val repository = mockk<SoundscapeRepository>(relaxed = true)
    private lateinit var viewModel: SoundscapeCategoryComposerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val categoryId = 1L

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getCategoryById(categoryId) } returns SoundscapeCategory(id = categoryId, name = "Test")
        every { repository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        val savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryId))
        viewModel = SoundscapeCategoryComposerViewModel(repository, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state becomes Success`() = runTest {
        val state = viewModel.uiState.first { it is SoundscapeCategoryComposerUiState.Success }
        assertThat(state).isInstanceOf(SoundscapeCategoryComposerUiState.Success::class.java)
    }

    @Test
    fun `addTrack calls repository upsertTrack`() = runTest {
        val name = "New Track"
        val path = "path"
        
        viewModel.addTrack(name, path)
        
        coVerify { repository.upsertTrack(match { it.name == name && it.filePath == path && it.categoryId == categoryId }) }
    }

    @Test
    fun `updateTrackIntensity calls repository upsertTrack`() = runTest {
        val track = SoundscapeTrack(id = 10L, categoryId = categoryId, name = "T", filePath = "P", intensityLevel = IntensityLevel.I)
        
        viewModel.updateTrackIntensity(track, IntensityLevel.II)
        
        coVerify { repository.upsertTrack(match { it.id == 10L && it.intensityLevel == IntensityLevel.II }) }
    }
}
