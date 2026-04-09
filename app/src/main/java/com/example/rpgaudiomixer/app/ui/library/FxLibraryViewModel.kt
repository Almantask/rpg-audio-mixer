package com.example.rpgaudiomixer.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val repository: FxRepository,
    private val audioPlayer: MixedMusicPlayer
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState: StateFlow<FxLibraryUiState> = combine(
        _searchQuery.flatMapLatest { query ->
            if (query.isBlank()) repository.observeAll()
            else repository.search(query)
        },
        _searchQuery
    ) { tracks, query ->
        FxLibraryUiState(
            tracks = tracks,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FxLibraryUiState(isLoading = true)
    )

    val isPreviewing = audioPlayer.isPreviewing
    val currentPreviewTitle = audioPlayer.currentPreviewTitle

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun importFx(name: String, filePath: String, durationMs: Long) {
        viewModelScope.launch {
            repository.upsert(
                FxTrack(
                    name = name,
                    filePath = filePath,
                    tags = emptyList(),
                    durationMs = durationMs
                )
            )
        }
    }

    fun updateFx(track: FxTrack) {
        viewModelScope.launch {
            repository.upsert(track)
        }
    }

    fun deleteFx(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun togglePreview(track: FxTrack) {
        if (currentPreviewTitle.value == track.name && isPreviewing.value) {
            audioPlayer.pausePreview()
        } else {
            audioPlayer.playPreview(track.filePath, track.name)
        }
    }

    fun stopPreview() {
        audioPlayer.stopPreview()
    }
}
