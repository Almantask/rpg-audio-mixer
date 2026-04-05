package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.repository.FXRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FXLibraryUiState(
    val tracks: List<FXTrack> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val previewingTrack: FXTrack? = null,
    val isPlayingPreview: Boolean = false
)

@HiltViewModel
class FXLibraryViewModel @Inject constructor(
    private val repository: FXRepository,
    private val trackFactory: TrackFactory
) : ViewModel() {

    private val _uiState = MutableStateFlow(FXLibraryUiState())
    val uiState: StateFlow<FXLibraryUiState> = _uiState.asStateFlow()

    private var currentPreviewPlayer: TrackPlayer? = null

    init {
        observeTracks()
    }

    private fun observeTracks() {
        _uiState.update { it.copy(isLoading = true) }
        repository.observeAll()
            .onEach { tracks ->
                _uiState.update { it.copy(tracks = tracks, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            observeTracks()
        } else {
            viewModelScope.launch {
                repository.search(query)
                    .collect { tracks ->
                        _uiState.update { it.copy(tracks = tracks) }
                    }
            }
        }
    }

    fun importFX(uri: Uri, name: String) {
        viewModelScope.launch {
            val newTrack = FXTrack(
                name = name,
                filePath = uri.toString(),
                tags = emptyList(),
                isOneShot = true
                // In a real app we'd measure duration here
            )
            repository.upsert(newTrack)
        }
    }

    fun updateFX(track: FXTrack) {
        viewModelScope.launch {
            repository.upsert(track)
        }
    }

    fun deleteFX(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
            if (_uiState.value.previewingTrack?.id == id) {
                stopPreview()
            }
        }
    }

    fun togglePreview(track: FXTrack) {
        if (_uiState.value.previewingTrack?.id == track.id) {
            if (_uiState.value.isPlayingPreview) {
                currentPreviewPlayer?.stop()
                _uiState.update { it.copy(isPlayingPreview = false) }
            } else {
                currentPreviewPlayer?.play()
                _uiState.update { it.copy(isPlayingPreview = true) }
            }
        } else {
            stopPreview()
            val player = trackFactory.createOneTimeTrackPlayer(track.filePath)
            currentPreviewPlayer = player
            _uiState.update { it.copy(previewingTrack = track, isPlayingPreview = true) }
            player.play()
        }
    }

    fun stopPreview() {
        currentPreviewPlayer?.stop()
        currentPreviewPlayer?.release()
        currentPreviewPlayer = null
        _uiState.update { it.copy(previewingTrack = null, isPlayingPreview = false) }
    }

    override fun onCleared() {
        super.onCleared()
        stopPreview()
    }
}
