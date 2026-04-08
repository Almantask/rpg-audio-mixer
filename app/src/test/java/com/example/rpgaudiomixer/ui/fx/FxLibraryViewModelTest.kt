package com.example.rpgaudiomixer.ui.fx

import com.example.rpgaudiomixer.domain.fx.FxPreviewPlayer
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FxLibraryViewModelTest {

    @Test
    fun init_exposes_tracks_and_demo_state() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        repository.tracksFlow.value = listOf(
            FxTrack(
                id = 1L,
                name = "Wolf Howl",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = listOf("Creature"),
                durationMs = 3000L,
                playCount = 0,
                isDemo = false,
            )
        )

        // Act
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.visibleTracks).containsExactlyElementsOf(repository.tracksFlow.value)
        assertThat(viewModel.uiState.value.showDemoButton).isTrue()
    }

    @Test
    fun updateSearchQuery_filters_tracks_by_name() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        repository.tracksFlow.value = listOf(
            FxTrack(1L, "Wolf Howl", "file:///fx/wolf.mp3", listOf("Creature"), 3000L, 0, false),
            FxTrack(2L, "Thunder Crack", "file:///fx/thunder.mp3", listOf("Weather"), 3200L, 0, false),
        )
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.updateSearchQuery("wolf")

        // Assert
        assertThat(viewModel.uiState.value.visibleTracks).containsExactly(repository.tracksFlow.value.first())
    }

    @Test
    fun selectTag_filters_tracks_by_tag() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        repository.tracksFlow.value = listOf(
            FxTrack(1L, "Wolf Howl", "file:///fx/wolf.mp3", listOf("Creature"), 3000L, 0, false),
            FxTrack(2L, "Thunder Crack", "file:///fx/thunder.mp3", listOf("Weather"), 3200L, 0, false),
        )
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.selectTag("Weather")

        // Assert
        assertThat(viewModel.uiState.value.visibleTracks).containsExactly(repository.tracksFlow.value.last())
    }

    @Test
    fun importFxTrack_adds_the_imported_track_to_the_visible_list() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.importFxTrack("content://device/wolf_howl.mp3")
        advanceUntilIdle()

        // Assert
        assertThat(repository.importRequests).containsExactly("content://device/wolf_howl.mp3")
        assertThat(viewModel.uiState.value.visibleTracks).containsExactly(repository.importedTrack)
    }

    @Test
    fun importFxTrack_exposes_an_error_when_import_fails() = runTest {
        // Arrange
        val repository = FakeFxRepository().apply { importError = IllegalArgumentException("Invalid audio") }
        val previewPlayer = FakeFxPreviewPlayer()
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.importFxTrack("content://device/fake.mp3")
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Invalid audio")
    }

    @Test
    fun saveEditorChanges_updates_the_track_and_closes_the_editor() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        repository.tracksFlow.value = listOf(
            FxTrack(1L, "wolf_howl.mp3", "file:///fx/wolf.mp3", emptyList(), 3000L, 0, false)
        )
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.openEditor(repository.tracksFlow.value.first())
        viewModel.updateEditorName("Wolf Howl")
        viewModel.toggleEditorTag("Combat")

        // Act
        viewModel.saveEditorChanges()
        advanceUntilIdle()

        // Assert
        assertThat(repository.updatedTracks).containsExactly(
            FxTrack(1L, "Wolf Howl", "file:///fx/wolf.mp3", listOf("Combat"), 3000L, 0, false)
        )
        assertThat(viewModel.uiState.value.editorState).isNull()
    }

    @Test
    fun deleteEditedTrack_soft_deletes_it_and_closes_the_editor() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val track = FxTrack(1L, "Wolf Howl", "file:///fx/wolf.mp3", emptyList(), 3000L, 0, false)
        repository.tracksFlow.value = listOf(track)
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.openEditor(track)

        // Act
        viewModel.deleteEditedTrack()
        advanceUntilIdle()

        // Assert
        assertThat(repository.softDeletedTrackIds).containsExactly(1L)
        assertThat(viewModel.uiState.value.editorState).isNull()
    }

    @Test
    fun downloadDemoFxTracks_hides_the_demo_button_when_complete() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.downloadDemoFxTracks()
        advanceUntilIdle()

        // Assert
        assertThat(repository.demoSeeded).isTrue()
        assertThat(viewModel.uiState.value.showDemoButton).isFalse()
    }

    @Test
    fun playPreview_shows_the_mini_player_with_the_selected_track() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val track = FxTrack(1L, "Thunder Crack", "file:///fx/thunder.mp3", emptyList(), 3200L, 0, false)
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.playPreview(track)
        advanceUntilIdle()

        // Assert
        assertThat(previewPlayer.playedPaths).containsExactly("file:///fx/thunder.mp3")
        assertThat(viewModel.uiState.value.previewState).isEqualTo(
            FxPreviewUiState(
                isVisible = true,
                currentTrackName = "Thunder Crack",
                isPlaying = true,
            )
        )
    }

    @Test
    fun pausePreview_keeps_the_mini_player_visible_and_marks_it_paused() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val track = FxTrack(1L, "Thunder Crack", "file:///fx/thunder.mp3", emptyList(), 3200L, 0, false)
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.playPreview(track)
        advanceUntilIdle()

        // Act
        viewModel.pausePreview()

        // Assert
        assertThat(previewPlayer.pauseCalls).isEqualTo(1)
        assertThat(viewModel.uiState.value.previewState).isEqualTo(
            FxPreviewUiState(
                isVisible = true,
                currentTrackName = "Thunder Crack",
                isPlaying = false,
            )
        )
    }

    @Test
    fun stopPreview_hides_the_mini_player_and_stops_audio() = runTest {
        // Arrange
        val repository = FakeFxRepository()
        val previewPlayer = FakeFxPreviewPlayer()
        val track = FxTrack(1L, "Thunder Crack", "file:///fx/thunder.mp3", emptyList(), 3200L, 0, false)
        val viewModel = FxLibraryViewModel(
            fxRepository = repository,
            fxPreviewPlayer = previewPlayer,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.playPreview(track)
        advanceUntilIdle()

        // Act
        viewModel.stopPreview()

        // Assert
        assertThat(previewPlayer.stopCalls).isEqualTo(1)
        assertThat(viewModel.uiState.value.previewState).isEqualTo(FxPreviewUiState())
    }

    private class FakeFxRepository : FxRepository {
        val tracksFlow = MutableStateFlow<List<FxTrack>>(emptyList())
        val demoAvailabilityFlow = MutableStateFlow(false)
        val importRequests = mutableListOf<String>()
        val updatedTracks = mutableListOf<FxTrack>()
        val softDeletedTrackIds = mutableListOf<Long>()
        var demoSeeded = false
        var importError: Throwable? = null
        val importedTrack = FxTrack(
            id = 99L,
            name = "wolf_howl.mp3",
            filePath = "file:///fx/wolf_howl.mp3",
            tags = emptyList(),
            durationMs = 3000L,
            playCount = 0,
            isDemo = false,
        )

        override fun observeFxTracks(): Flow<List<FxTrack>> = tracksFlow

        override fun searchFxTracks(query: String): Flow<List<FxTrack>> = tracksFlow

        override fun observeHasDemoFxTracks(): Flow<Boolean> = demoAvailabilityFlow

        override suspend fun importFxTrack(sourceUri: String): FxTrack {
            importRequests += sourceUri
            importError?.let { throw it }
            tracksFlow.value = tracksFlow.value + importedTrack
            return importedTrack
        }

        override suspend fun updateFxTrack(track: FxTrack) {
            updatedTracks += track
            tracksFlow.value = tracksFlow.value.map { if (it.id == track.id) track else it }
        }

        override suspend fun softDeleteFxTrack(trackId: Long) {
            softDeletedTrackIds += trackId
            tracksFlow.value = tracksFlow.value.filterNot { it.id == trackId }
        }

        override suspend fun seedDemoFxTracks() {
            demoSeeded = true
            demoAvailabilityFlow.value = true
        }
    }

    private class FakeFxPreviewPlayer : FxPreviewPlayer {
        val playedPaths = mutableListOf<String>()
        var pauseCalls = 0
        var stopCalls = 0

        override fun play(filePath: String) {
            playedPaths += filePath
        }

        override fun pause() {
            pauseCalls += 1
        }

        override fun stop() {
            stopCalls += 1
        }
    }
}
