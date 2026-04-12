package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayer
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayerFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LibraryViewModelTest {

    private val mockPlayer: SimpleAudioPlayer = mockk(relaxed = true)
    private val mockFactory: SimpleAudioPlayerFactory = mockk {
        every { create() } returns mockPlayer
    }

    private lateinit var viewModel: LibraryViewModel

    @BeforeEach
    fun setUp() {
        viewModel = LibraryViewModel(mockFactory)
    }

    // --- Initial state ---

    @Test
    fun `initial state is empty`() {
        // Arrange — viewModel already created in setUp

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(LibraryUiState.Empty)
    }

    // --- addFile ---

    @Test
    fun `addFile transitions to content state`() {
        // Arrange
        val uri = mockk<Uri>()

        // Act
        viewModel.addFile(uri, "battle.mp3")

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(LibraryUiState.Content::class.java)
        val content = state as LibraryUiState.Content
        assertThat(content.files).hasSize(1)
        assertThat(content.files[0]).isEqualTo(AudioFileItem(uri, "battle.mp3"))
    }

    @Test
    fun `addFile multiple files shows all`() {
        // Arrange
        val uri1 = mockk<Uri>()
        val uri2 = mockk<Uri>()
        val uri3 = mockk<Uri>()

        // Act
        viewModel.addFile(uri1, "battle.mp3")
        viewModel.addFile(uri2, "tavern.ogg")
        viewModel.addFile(uri3, "forest.wav")

        // Assert
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.files).hasSize(3)
        assertThat(state.files.map { it.displayName })
            .containsExactly("battle.mp3", "tavern.ogg", "forest.wav")
    }

    // --- removeFile ---

    @Test
    fun `removeFile removes from list`() {
        // Arrange
        val uri1 = mockk<Uri>()
        val uri2 = mockk<Uri>()
        viewModel.addFile(uri1, "battle.mp3")
        viewModel.addFile(uri2, "tavern.ogg")

        // Act
        viewModel.removeFile(uri1)

        // Assert
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.files).hasSize(1)
        assertThat(state.files[0].displayName).isEqualTo("tavern.ogg")
    }

    @Test
    fun `removeFile last file returns to empty`() {
        // Arrange
        val uri = mockk<Uri>()
        viewModel.addFile(uri, "battle.mp3")

        // Act
        viewModel.removeFile(uri)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(LibraryUiState.Empty)
    }

    @Test
    fun `removeFile stops playback when removing currently playing file`() {
        // Arrange
        val uri = mockk<Uri>()
        every { mockPlayer.currentUri } returns uri
        every { mockPlayer.isPlaying } returns true
        viewModel.addFile(uri, "battle.mp3")
        viewModel.playPreview(uri)

        // Act
        viewModel.removeFile(uri)

        // Assert
        verify { mockPlayer.stop() }
        assertThat(viewModel.uiState.value).isEqualTo(LibraryUiState.Empty)
    }

    // --- playPreview ---

    @Test
    fun `playPreview starts playback`() {
        // Arrange
        val uri = mockk<Uri>()
        viewModel.addFile(uri, "battle.mp3")
        every { mockPlayer.isPlaying } returns false
        every { mockPlayer.currentUri } returns null

        // Act
        viewModel.playPreview(uri)

        // Assert
        verify { mockPlayer.play(uri) }
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.playingUri).isSameAs(uri)
    }

    @Test
    fun `playPreview same uri toggles to pause when playing`() {
        // Arrange
        val uri = mockk<Uri>()
        viewModel.addFile(uri, "battle.mp3")
        every { mockPlayer.currentUri } returns uri
        every { mockPlayer.isPlaying } returns true

        // Act
        viewModel.playPreview(uri)

        // Assert
        verify { mockPlayer.pause() }
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.playingUri).isNull()
    }

    @Test
    fun `playPreview same uri resumes when paused`() {
        // Arrange
        val uri = mockk<Uri>()
        viewModel.addFile(uri, "battle.mp3")
        // First: playing
        every { mockPlayer.currentUri } returns uri
        every { mockPlayer.isPlaying } returns true
        viewModel.playPreview(uri) // toggles to pause

        // Now simulate paused state
        every { mockPlayer.isPlaying } returns false

        // Act — toggle again should resume
        viewModel.playPreview(uri)

        // Assert
        verify { mockPlayer.play(uri) }
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.playingUri).isSameAs(uri)
    }

    @Test
    fun `playPreview different uri switches playback`() {
        // Arrange
        val uri1 = mockk<Uri>()
        val uri2 = mockk<Uri>()
        viewModel.addFile(uri1, "battle.mp3")
        viewModel.addFile(uri2, "tavern.ogg")
        every { mockPlayer.currentUri } returns uri1
        every { mockPlayer.isPlaying } returns false

        // Act
        viewModel.playPreview(uri2)

        // Assert
        verify { mockPlayer.play(uri2) }
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.playingUri).isSameAs(uri2)
    }

    // --- stopPreview ---

    @Test
    fun `stopPreview stops playback`() {
        // Arrange
        val uri = mockk<Uri>()
        viewModel.addFile(uri, "battle.mp3")
        every { mockPlayer.isPlaying } returns false
        every { mockPlayer.currentUri } returns null
        viewModel.playPreview(uri)

        // Act
        viewModel.stopPreview()

        // Assert
        verify { mockPlayer.stop() }
        val state = viewModel.uiState.value as LibraryUiState.Content
        assertThat(state.playingUri).isNull()
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
