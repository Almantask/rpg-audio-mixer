package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.app.data.storage.FileStorageManager
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayer
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayerFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val trackFlow = MutableSharedFlow<List<AudioTrack>>(replay = 1)

    private val mockPlayer: SimpleAudioPlayer = mockk(relaxed = true)
    private val mockFactory: SimpleAudioPlayerFactory = mockk {
        every { create() } returns mockPlayer
    }
    private val mockRepository: AudioTrackRepository = mockk {
        every { observeAll() } returns trackFlow
        coEvery { addTrack(any(), any()) } just Runs
        coEvery { deleteTrack(any()) } just Runs
        coEvery { deleteAll() } just Runs
    }
    private val mockFileStorageManager: FileStorageManager = mockk {
        coEvery { copyToInternalStorage(any(), any()) } returns "file:///audio/battle.mp3"
    }

    private lateinit var viewModel: LibraryViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LibraryViewModel(mockFactory, mockRepository, mockFileStorageManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Initial state ---

    @Test
    fun `initial state is empty when repository emits nothing`() = runTest(testDispatcher) {
        // Arrange — viewModel already created in setUp

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(LibraryUiState.Empty)
    }

    @Test
    fun `uiState transitions to empty when repository emits empty list`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        trackFlow.emit(emptyList())

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(LibraryUiState.Empty)
    }

    // --- Repository-driven state ---

    @Test
    fun `uiState transitions to Content when repository emits tracks`() = runTest(testDispatcher) {
        // Arrange
        val tracks = listOf(AudioTrack(id = 1, uri = "file:///audio/battle.mp3", displayName = "battle.mp3"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        trackFlow.emit(tracks)

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(LibraryUiState.Content::class.java)
        val content = state as LibraryUiState.Content
        assertThat(content.tracks).hasSize(1)
        assertThat(content.tracks[0].displayName).isEqualTo("battle.mp3")
        assertThat(content.playingUri).isNull()
    }

    @Test
    fun `uiState shows all tracks when repository emits multiple`() = runTest(testDispatcher) {
        // Arrange
        val tracks = listOf(
            AudioTrack(id = 1, uri = "file:///audio/battle.mp3", displayName = "battle.mp3"),
            AudioTrack(id = 2, uri = "file:///audio/tavern.ogg", displayName = "tavern.ogg")
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        trackFlow.emit(tracks)

        // Assert
        val content = viewModel.uiState.value as LibraryUiState.Content
        assertThat(content.tracks).hasSize(2)
        assertThat(content.tracks.map { it.displayName })
            .containsExactly("battle.mp3", "tavern.ogg")
    }

    // --- addFile ---

    @Test
    fun `addFile copies file to internal storage then adds track to repository`() = runTest(testDispatcher) {
        // Arrange
        val uri = mockk<Uri>()
        coEvery { mockFileStorageManager.copyToInternalStorage(uri, "battle.mp3") } returns "file:///audio/battle.mp3"

        // Act
        viewModel.addFile(uri, "battle.mp3")

        // Assert
        coVerify { mockFileStorageManager.copyToInternalStorage(uri, "battle.mp3") }
        coVerify { mockRepository.addTrack("file:///audio/battle.mp3", "battle.mp3") }
    }

    // --- removeTrack ---

    @Test
    fun `removeTrack delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val track = AudioTrack(id = 5, uri = "file:///audio/battle.mp3", displayName = "battle.mp3")

        // Act
        viewModel.removeTrack(track)

        // Assert
        coVerify { mockRepository.deleteTrack(track) }
    }

    @Test
    fun `removeTrack stops playback when removing currently playing track`() = runTest(testDispatcher) {
        // Arrange
        val uriString = "file:///audio/battle.mp3"
        val mockUri = mockk<Uri>()
        every { mockUri.toString() } returns uriString
        val track = AudioTrack(id = 5, uri = uriString, displayName = "battle.mp3")
        every { mockPlayer.isPlaying } returns false
        viewModel.playPreview(mockUri)
        every { mockPlayer.isPlaying } returns true

        // Act
        viewModel.removeTrack(track)

        // Assert
        verify { mockPlayer.stop() }
        coVerify { mockRepository.deleteTrack(track) }
    }

    // --- playPreview ---

    @Test
    fun `playPreview starts playback and sets playingUri`() = runTest(testDispatcher) {
        // Arrange
        val uriString = "file:///audio/battle.mp3"
        val mockUri = mockk<Uri>()
        every { mockUri.toString() } returns uriString
        val tracks = listOf(AudioTrack(id = 1, uri = uriString, displayName = "battle.mp3"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        trackFlow.emit(tracks)
        every { mockPlayer.isPlaying } returns false

        // Act
        viewModel.playPreview(mockUri)

        // Assert
        verify { mockPlayer.play(mockUri) }
        val content = viewModel.uiState.value as LibraryUiState.Content
        assertThat(content.playingUri).isEqualTo(uriString)
    }

    @Test
    fun `playPreview same uri pauses when already playing`() = runTest(testDispatcher) {
        // Arrange
        val uriString = "file:///audio/battle.mp3"
        val mockUri = mockk<Uri>()
        every { mockUri.toString() } returns uriString
        val tracks = listOf(AudioTrack(id = 1, uri = uriString, displayName = "battle.mp3"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        trackFlow.emit(tracks)
        every { mockPlayer.isPlaying } returns false
        viewModel.playPreview(mockUri) // set playing

        // Act — play same uri again while playing
        every { mockPlayer.isPlaying } returns true
        viewModel.playPreview(mockUri)

        // Assert
        verify { mockPlayer.pause() }
        val content = viewModel.uiState.value as LibraryUiState.Content
        assertThat(content.playingUri).isNull()
    }

    // --- stopPreview ---

    @Test
    fun `stopPreview stops audio and clears playingUri`() = runTest(testDispatcher) {
        // Arrange
        val uriString = "file:///audio/battle.mp3"
        val mockUri = mockk<Uri>()
        every { mockUri.toString() } returns uriString
        val tracks = listOf(AudioTrack(id = 1, uri = uriString, displayName = "battle.mp3"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        trackFlow.emit(tracks)
        every { mockPlayer.isPlaying } returns false
        viewModel.playPreview(mockUri)

        // Act
        viewModel.stopPreview()

        // Assert
        verify { mockPlayer.stop() }
        val content = viewModel.uiState.value as LibraryUiState.Content
        assertThat(content.playingUri).isNull()
    }

    // --- onCleared ---

    @Test
    fun `onCleared releases player`() {
        // Arrange — viewModel created in setUp

        // Act — call protected onCleared via reflection
        val method = ViewModel::class.java.getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(viewModel)

        // Assert
        verify { mockPlayer.release() }
    }
}

