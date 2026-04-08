package com.example.rpgaudiomixer.ui.fx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for FX Library screen.
 */
sealed class FxLibraryUiState {
    object Loading : FxLibraryUiState()
    data class Success(val fxTracks: List<FxTrack>) : FxLibraryUiState()
    data class Error(val message: String) : FxLibraryUiState()
}

/**
 * ViewModel for FX Library screen.
 *
 * Manages FX track list, search, and CRUD operations.
 */
@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FxLibraryUiState>(FxLibraryUiState.Loading)
    val uiState: StateFlow<FxLibraryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadFxTracks()
    }

    private fun loadFxTracks() {
        viewModelScope.launch {
            fxRepository.observeAll()
                .catch { e ->
                    _uiState.value = FxLibraryUiState.Error(
                        e.message ?: "Failed to load FX tracks"
                    )
                }
                .collect { tracks ->
                    _uiState.value = FxLibraryUiState.Success(tracks)
                }
        }
    }

    /**
     * Search FX tracks by name or tags.
     */
    fun search(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadFxTracks()
            return
        }

        viewModelScope.launch {
            fxRepository.search(query)
                .catch { e ->
                    _uiState.value = FxLibraryUiState.Error(
                        e.message ?: "Failed to search FX tracks"
                    )
                }
                .collect { tracks ->
                    _uiState.value = FxLibraryUiState.Success(tracks)
                }
        }
    }

    /**
     * Import a new FX track.
     */
    fun importFxTrack(name: String, filePath: String, tags: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                fxRepository.create(name, filePath, tags)
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(
                    e.message ?: "Failed to import FX track"
                )
            }
        }
    }

    /**
     * Delete an FX track (soft-delete).
     */
    fun deleteFxTrack(id: Long) {
        viewModelScope.launch {
            try {
                fxRepository.delete(id)
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(
                    e.message ?: "Failed to delete FX track"
                )
            }
        }
    }

    /**
     * Clear the error state.
     */
    fun clearError() {
        if (_uiState.value is FxLibraryUiState.Error) {
            viewModelScope.launch {
                loadFxTracks()
            }
        }
    }
}
