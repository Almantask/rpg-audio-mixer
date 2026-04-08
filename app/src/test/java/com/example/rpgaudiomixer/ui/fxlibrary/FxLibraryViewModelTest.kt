package com.example.rpgaudiomixer.ui.fxlibrary

import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FxLibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fxRepository: FxRepository = mockk()

    private lateinit var viewModel: FxLibraryViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange & Act
        viewModel = FxLibraryViewModel(fxRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(FxLibraryUiState.Loading::class.java)
    }

    @Test
    fun `when tracks exist, state shows track list`() = runTest {
        // Arrange
        val tracks = listOf(
            FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = listOf("Combat"), durationMs = 3000L),
            FxTrack(id = "2", name = "Thunder Crack", filePath = "/path/thunder.mp3", tags = listOf("Weather"), durationMs = 2500L)
        )
        coEvery { fxRepository.getAllFxTracks() } returns tracks

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(2)
        assertThat(state.tracks[0].name).isEqualTo("Wolf Howl")
        assertThat(state.tracks[1].name).isEqualTo("Thunder Crack")
    }

    @Test
    fun `when no tracks exist, state shows empty list`() = runTest {
        // Arrange
        coEvery { fxRepository.getAllFxTracks() } returns emptyList()

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).isEmpty()
    }

    @Test
    fun `importFxTrack calls repository and refreshes list`() = runTest {
        // Arrange
        val existingTracks = listOf(
            FxTrack(id = "1", name = "Existing Track", filePath = "/path/existing.mp3", tags = emptyList())
        )
        val newTrack = FxTrack(id = "2", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = listOf("Combat"))

        coEvery { fxRepository.getAllFxTracks() } returns existingTracks andThen listOf(newTrack, existingTracks[0])
        coEvery { fxRepository.importFxTrack("Wolf Howl", "/path/wolf.mp3", listOf("Combat")) } returns newTrack

        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.importFxTrack("Wolf Howl", "/path/wolf.mp3", listOf("Combat"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { fxRepository.importFxTrack("Wolf Howl", "/path/wolf.mp3", listOf("Combat")) }
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(2)
        assertThat(state.tracks[0].name).isEqualTo("Wolf Howl")
    }

    @Test
    fun `updateFxTrack calls repository and refreshes list`() = runTest {
        // Arrange
        val originalTrack = FxTrack(id = "1", name = "wolf_howl.mp3", filePath = "/path/wolf.mp3", tags = emptyList())
        val updatedTrack = FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = listOf("Combat"))

        coEvery { fxRepository.getAllFxTracks() } returns listOf(originalTrack) andThen listOf(updatedTrack)
        coEvery { fxRepository.updateFxTrack(updatedTrack) } returns Unit

        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateFxTrack(updatedTrack)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { fxRepository.updateFxTrack(updatedTrack) }
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks[0].name).isEqualTo("Wolf Howl")
        assertThat(state.tracks[0].tags).contains("Combat")
    }

    @Test
    fun `deleteFxTrack calls repository and refreshes list`() = runTest {
        // Arrange
        val tracks = listOf(
            FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = emptyList()),
            FxTrack(id = "2", name = "Thunder Crack", filePath = "/path/thunder.mp3", tags = emptyList())
        )
        coEvery { fxRepository.getAllFxTracks() } returns tracks andThen listOf(tracks[1])
        coEvery { fxRepository.deleteFxTrack("1") } returns Unit

        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteFxTrack("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { fxRepository.deleteFxTrack("1") }
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(1)
        assertThat(state.tracks[0].id).isEqualTo("2")
    }

    @Test
    fun `searchFxTracks filters tracks by query`() = runTest {
        // Arrange
        val allTracks = listOf(
            FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = listOf("Combat")),
            FxTrack(id = "2", name = "Thunder Crack", filePath = "/path/thunder.mp3", tags = listOf("Weather")),
            FxTrack(id = "3", name = "Door Creak", filePath = "/path/door.mp3", tags = listOf("Ambient"))
        )
        val filteredTracks = listOf(allTracks[0])

        coEvery { fxRepository.getAllFxTracks() } returns allTracks
        coEvery { fxRepository.searchFxTracks("Wolf") } returns filteredTracks

        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.searchFxTracks("Wolf")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { fxRepository.searchFxTracks("Wolf") }
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(1)
        assertThat(state.tracks[0].name).isEqualTo("Wolf Howl")
    }

    @Test
    fun `searchFxTracks with empty query shows all tracks`() = runTest {
        // Arrange
        val allTracks = listOf(
            FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = listOf("Combat")),
            FxTrack(id = "2", name = "Thunder Crack", filePath = "/path/thunder.mp3", tags = listOf("Weather"))
        )

        coEvery { fxRepository.getAllFxTracks() } returns allTracks
        coEvery { fxRepository.searchFxTracks("Wolf") } returns listOf(allTracks[0])

        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - first search, then clear
        viewModel.searchFxTracks("Wolf")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.clearSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(2)
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { fxRepository.getAllFxTracks() } throws Exception(errorMessage)

        // Act
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }

    @Test
    fun `showImportDialog updates dialog state`() {
        // Arrange
        coEvery { fxRepository.getAllFxTracks() } returns emptyList()
        viewModel = FxLibraryViewModel(fxRepository)

        // Act
        viewModel.showImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isTrue()
    }

    @Test
    fun `hideImportDialog updates dialog state`() {
        // Arrange
        coEvery { fxRepository.getAllFxTracks() } returns emptyList()
        viewModel = FxLibraryViewModel(fxRepository)
        viewModel.showImportDialog()

        // Act
        viewModel.hideImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isFalse()
    }

    @Test
    fun `showEditDialog updates dialog state with track`() {
        // Arrange
        val track = FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = emptyList())
        coEvery { fxRepository.getAllFxTracks() } returns emptyList()
        viewModel = FxLibraryViewModel(fxRepository)

        // Act
        viewModel.showEditDialog(track)

        // Assert
        assertThat(viewModel.showEditDialog.value).isTrue()
        assertThat(viewModel.editingTrack.value).isEqualTo(track)
    }

    @Test
    fun `hideEditDialog updates dialog state and clears editing track`() {
        // Arrange
        val track = FxTrack(id = "1", name = "Wolf Howl", filePath = "/path/wolf.mp3", tags = emptyList())
        coEvery { fxRepository.getAllFxTracks() } returns emptyList()
        viewModel = FxLibraryViewModel(fxRepository)
        viewModel.showEditDialog(track)

        // Act
        viewModel.hideEditDialog()

        // Assert
        assertThat(viewModel.showEditDialog.value).isFalse()
        assertThat(viewModel.editingTrack.value).isNull()
    }

    @Test
    fun `downloadDemoFxTracks sets downloading state and refreshes list`() = runTest {
        // Arrange
        val demoTracks = List(100) { index ->
            FxTrack(
                id = "demo-$index",
                name = "Demo FX $index",
                filePath = "/path/demo_$index.mp3",
                tags = listOf("Demo")
            )
        }

        coEvery { fxRepository.getAllFxTracks() } returns emptyList() andThen demoTracks
        // Simulate download method - this would be in the repository or a use case
        viewModel = FxLibraryViewModel(fxRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.downloadDemoFxTracks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as FxLibraryUiState.Success
        assertThat(state.tracks).hasSize(100)
        assertThat(viewModel.isDownloadingDemo.value).isFalse()
    }
}
