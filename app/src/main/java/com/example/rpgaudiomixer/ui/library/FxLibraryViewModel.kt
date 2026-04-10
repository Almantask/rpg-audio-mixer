package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FxPreviewState(
    val trackName: String = "",
    val isVisible: Boolean = false,
    val isPlaying: Boolean = false,
)

sealed interface FxLibraryUiState {
    data object Loading : FxLibraryUiState

    data class Success(
        val tracks: List<FxTrack>,
        val searchQuery: String,
        val previewState: FxPreviewState,
        val isDemoDownloadVisible: Boolean,
        val isDownloadingDemoTracks: Boolean,
    ) : FxLibraryUiState

    data class Error(val message: String) : FxLibraryUiState
}

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val repository: FxRepository,
    private val musicPlayer: MixedMusicPlayer,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val previewState = MutableStateFlow(FxPreviewState())
    private val errorMessage = MutableStateFlow<String?>(null)
    private val isDownloadingDemoTracks = MutableStateFlow(false)
    private val allTracks = MutableStateFlow<List<FxTrack>>(emptyList())
    private var currentPreviewTrackId: Long? = null

    val uiState: StateFlow<FxLibraryUiState> = combine(
        allTracks,
        searchQuery,
        previewState,
        isDownloadingDemoTracks,
        errorMessage,
    ) { tracks, query, preview, isDownloading, error ->
        error?.let {
            FxLibraryUiState.Error(it)
        } ?: FxLibraryUiState.Success(
            tracks = tracks.filter { track ->
                query.isBlank() ||
                    track.name.contains(query, ignoreCase = true) ||
                    track.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            },
            searchQuery = query,
            previewState = preview,
            isDemoDownloadVisible = tracks.none { it.isDemoContent },
            isDownloadingDemoTracks = isDownloading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = FxLibraryUiState.Loading,
    )

    init {
        viewModelScope.launch {
            repository.observeTracks().collect { tracks ->
                allTracks.value = tracks
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun importTrack(name: String, filePath: String) {
        viewModelScope.launch {
            repository.importTrack(name = name, filePath = filePath)
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "The file could not be read as audio."
                }
        }
    }

    fun downloadDemoTracks() {
        viewModelScope.launch {
            isDownloadingDemoTracks.value = true
            runCatching { repository.installDemoTracks() }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to download demo FX."
                }
            isDownloadingDemoTracks.value = false
        }
    }

    fun saveTrack(track: FxTrack) {
        viewModelScope.launch {
            runCatching { repository.updateTrack(track) }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to save FX."
                }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            if (currentPreviewTrackId == trackId) {
                onLibraryHidden()
            }
            runCatching { repository.deleteTrack(trackId) }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to delete FX."
                }
        }
    }

    fun previewTrack(track: FxTrack) {
        currentPreviewTrackId = track.id
        musicPlayer.previewSound(track.filePath)
        previewState.value = FxPreviewState(
            trackName = track.name,
            isVisible = true,
            isPlaying = true,
        )
    }

    fun togglePreviewPlayback() {
        val currentState = previewState.value
        if (!currentState.isVisible) {
            return
        }

        if (currentState.isPlaying) {
            musicPlayer.pausePreview()
            previewState.value = currentState.copy(isPlaying = false)
        } else {
            val track = allTracks.value.firstOrNull { it.id == currentPreviewTrackId } ?: return
            musicPlayer.previewSound(track.filePath)
            previewState.value = currentState.copy(isPlaying = true, trackName = track.name)
        }
    }

    fun previewPrevious() {
        val index = allTracks.value.indexOfFirst { it.id == currentPreviewTrackId }
        if (index <= 0) {
            return
        }
        previewTrack(allTracks.value[index - 1])
    }

    fun previewNext() {
        val index = allTracks.value.indexOfFirst { it.id == currentPreviewTrackId }
        if (index == -1 || index >= allTracks.value.lastIndex) {
            return
        }
        previewTrack(allTracks.value[index + 1])
    }

    fun dismissError() {
        errorMessage.value = null
    }

    fun onLibraryHidden() {
        musicPlayer.stopPreview()
        currentPreviewTrackId = null
        previewState.value = FxPreviewState()
    }
}
