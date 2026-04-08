package com.example.rpgaudiomixer.ui.library.fx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<FxTrack>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<FxTrack>>> = _uiState.asStateFlow()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            try {
                _searchQuery
                    .debounce(300)
                    .flatMapLatest { query ->
                        if (query.isBlank()) {
                            fxRepository.observeAll()
                        } else {
                            fxRepository.search(query)
                        }
                    }
                    .collect { tracks ->
                        _uiState.value = UiState.Success(tracks)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load FX tracks")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun importTrack(name: String, filePath: String, tags: List<String>, durationMs: Long) {
        viewModelScope.launch {
            try {
                fxRepository.create(name, filePath, tags, durationMs)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to import track")
            }
        }
    }

    fun updateTrack(track: FxTrack) {
        viewModelScope.launch {
            try {
                fxRepository.update(track)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update track")
            }
        }
    }

    fun deleteTrack(id: Long) {
        viewModelScope.launch {
            try {
                fxRepository.delete(id)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete track")
            }
        }
    }
}
