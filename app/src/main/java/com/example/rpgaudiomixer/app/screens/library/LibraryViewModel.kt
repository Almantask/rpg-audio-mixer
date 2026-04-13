package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.data.storage.FileStorageManager
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayer
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the Library screen. */
sealed interface LibraryUiState {
    data object Empty : LibraryUiState
    data class Content(
        val tracks: List<AudioTrack>,
        val playingUri: String?,
    ) : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    playerFactory: SimpleAudioPlayerFactory,
    private val audioTrackRepository: AudioTrackRepository,
    private val fileStorageManager: FileStorageManager,
) : ViewModel() {

    private val audioPlayer: SimpleAudioPlayer = playerFactory.create()

    private val _playingUri = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        audioTrackRepository.observeAll(),
        _playingUri
    ) { tracks, playingUri ->
        if (tracks.isEmpty()) LibraryUiState.Empty
        else LibraryUiState.Content(tracks = tracks, playingUri = playingUri)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState.Empty)

    // ── public API ──────────────────────────────────────────────

    fun addFile(uri: Uri, displayName: String) {
        viewModelScope.launch {
            val internalPath = fileStorageManager.copyToInternalStorage(uri, displayName)
            audioTrackRepository.addTrack(internalPath, displayName)
        }
    }

    fun removeTrack(track: AudioTrack) {
        if (_playingUri.value == track.uri && audioPlayer.isPlaying) {
            stopPreview()
        }
        viewModelScope.launch {
            audioTrackRepository.deleteTrack(track)
        }
    }

    fun playPreview(uri: Uri) {
        val uriString = uri.toString()
        if (_playingUri.value == uriString && audioPlayer.isPlaying) {
            audioPlayer.pause()
            _playingUri.value = null
        } else {
            audioPlayer.play(uri)
            _playingUri.value = uriString
        }
    }

    fun stopPreview() {
        audioPlayer.stop()
        _playingUri.value = null
    }

    // ── lifecycle ───────────────────────────────────────────────

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
