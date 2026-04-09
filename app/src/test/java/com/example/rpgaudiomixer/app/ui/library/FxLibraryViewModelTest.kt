package com.example.rpgaudiomixer.app.ui.library

import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FxLibraryViewModelTest {

    private val repository = mockk<FxRepository>(relaxed = true)
    private val audioPlayer = mockk<MixedMusicPlayer>(relaxed = true)
    private lateinit var viewModel: FxLibraryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeAll() } returns flowOf(emptyList())
        every { audioPlayer.isPreviewing } returns MutableStateFlow(false)
        every { audioPlayer.currentPreviewTitle } returns MutableStateFlow(null)
        viewModel = FxLibraryViewModel(repository, audioPlayer)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects empty library`() = runTest {
        val state = viewModel.uiState.first { !it.isLoading }
        assertThat(state.tracks).isEmpty()
    }

    @Test
    fun `search updates ui state`() = runTest {
        val query = "explosion"
        every { repository.search(query) } returns flowOf(listOf(FxTrack(name = "Explosion", filePath = "p", tags = emptyList(), durationMs = 1000)))
        
        viewModel.updateSearchQuery(query)
        
        val state = viewModel.uiState.first { it.searchQuery == query }
        assertThat(state.tracks).hasSize(1)
        assertThat(state.tracks[0].name).isEqualTo("Explosion")
    }

    @Test
    fun `togglePreview plays audio`() = runTest {
        val track = FxTrack(id = 1, name = "Boom", filePath = "path", tags = emptyList(), durationMs = 500)
        
        viewModel.togglePreview(track)
        
        verify { audioPlayer.playPreview("path", "Boom") }
    }
}
