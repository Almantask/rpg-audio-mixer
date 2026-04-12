package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayer
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Displayable metadata for a single imported audio file. */
data class AudioFileItem(
    val uri: Uri,
    val displayName: String,
)

/** UI state for the Library screen. */
sealed interface LibraryUiState {
    data object Empty : LibraryUiState
    data class Content(
        val files: List<AudioFileItem>,
        val playingUri: Uri?,
    ) : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    playerFactory: SimpleAudioPlayerFactory,
) : ViewModel() {

    private val audioPlayer: SimpleAudioPlayer = playerFactory.create()

    private val files = mutableListOf<AudioFileItem>()
    private var playingUri: Uri? = null

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Empty)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    // ── public API ──────────────────────────────────────────────

    fun addFile(uri: Uri, displayName: String) {
        files.add(AudioFileItem(uri, displayName))
        emitState()
    }

    fun removeFile(uri: Uri) {
        if (audioPlayer.currentUri == uri && audioPlayer.isPlaying) {
            stopPreview()
        }
        files.removeAll { it.uri == uri }
        emitState()
    }

    fun playPreview(uri: Uri) {
        if (audioPlayer.currentUri == uri && audioPlayer.isPlaying) {
            audioPlayer.pause()
            playingUri = null
        } else {
            audioPlayer.play(uri)
            playingUri = uri
        }
        emitState()
    }

    fun stopPreview() {
        audioPlayer.stop()
        playingUri = null
        emitState()
    }

    // ── lifecycle ───────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    // ── private helpers ─────────────────────────────────────────

    private fun emitState() {
        _uiState.value = if (files.isEmpty()) {
            LibraryUiState.Empty
        } else {
            LibraryUiState.Content(
                files = files.toList(),
                playingUri = playingUri,
            )
        }
    }
}
