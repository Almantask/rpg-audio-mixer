package com.example.rpgaudiomixer.ui.library

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class FxLibraryViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun previewTrack_updates_mini_player_state_and_starts_preview() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeFxRepository(
            initialTracks = listOf(
                FxTrack(
                    id = 1L,
                    name = "Thunder Crack",
                    filePath = "content://thunder",
                    tags = emptyList(),
                    durationMs = 2_000L,
                    playCount = 0,
                    isDemoContent = false,
                ),
            ),
        )
        val musicPlayer: MixedMusicPlayer = mockk(relaxed = true)
        every { musicPlayer.previewSound("content://thunder") } just runs
        val viewModel = FxLibraryViewModel(repository, musicPlayer)
        advanceUntilIdle()

        // Act
        viewModel.previewTrack(repository.currentTracks.single())
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.previewState).isEqualTo(
            FxPreviewState(
                trackName = "Thunder Crack",
                isVisible = true,
                isPlaying = true,
            ),
        )
        verify(exactly = 1) { musicPlayer.previewSound("content://thunder") }
    }

    @Test
    fun onLibraryHidden_stops_preview_and_hides_the_mini_player() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeFxRepository(
            initialTracks = listOf(
                FxTrack(
                    id = 1L,
                    name = "Thunder Crack",
                    filePath = "content://thunder",
                    tags = emptyList(),
                    durationMs = 2_000L,
                    playCount = 0,
                    isDemoContent = false,
                ),
            ),
        )
        val musicPlayer: MixedMusicPlayer = mockk(relaxed = true)
        every { musicPlayer.previewSound(any()) } just runs
        every { musicPlayer.stopPreview() } just runs
        val viewModel = FxLibraryViewModel(repository, musicPlayer)
        advanceUntilIdle()
        viewModel.previewTrack(repository.currentTracks.single())
        advanceUntilIdle()

        // Act
        viewModel.onLibraryHidden()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.previewState).isEqualTo(FxPreviewState())
        verify(exactly = 1) { musicPlayer.stopPreview() }
    }

    private class FakeFxRepository(
        initialTracks: List<FxTrack> = emptyList(),
    ) : FxRepository {
        private val tracksFlow = MutableStateFlow(initialTracks)
        val currentTracks: List<FxTrack>
            get() = tracksFlow.value

        override fun observeTracks(): Flow<List<FxTrack>> = tracksFlow

        override fun observeMostPlayedTrack(): Flow<FxTrack?> = MutableStateFlow(null)

        override suspend fun importTrack(name: String, filePath: String): Result<Long> = Result.success(1L)

        override suspend fun installDemoTracks() = Unit

        override suspend fun updateTrack(track: FxTrack) {
            tracksFlow.value = tracksFlow.value.map { if (it.id == track.id) track else it }
        }

        override suspend fun deleteTrack(trackId: Long) {
            tracksFlow.value = tracksFlow.value.filterNot { it.id == trackId }
        }
    }
}
