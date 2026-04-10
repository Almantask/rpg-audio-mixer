package com.example.rpgaudiomixer.ui.fx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FxLibraryUiState {
    data object Loading : FxLibraryUiState
    data class Success(val tracks: List<FxTrack>) : FxLibraryUiState
    data class Error(val message: String) : FxLibraryUiState
}

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val uiState: StateFlow<FxLibraryUiState> = combine(
        _searchQuery,
        fxRepository.observeAll()
    ) { query, allTracks ->
        if (query.isBlank()) {
            FxLibraryUiState.Success(allTracks) as FxLibraryUiState
        } else {
            val filtered = allTracks.filter { track ->
                track.name.contains(query, ignoreCase = true) ||
                track.tags.any { it.contains(query, ignoreCase = true) }
            }
            FxLibraryUiState.Success(filtered) as FxLibraryUiState
        }
    }
        .catch { e ->
            emit(FxLibraryUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FxLibraryUiState.Loading
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun importFxTrack(name: String, filePath: String, tags: List<String>) {
        viewModelScope.launch {
            try {
                val track = FxTrack(
                    name = name,
                    filePath = filePath,
                    tags = tags,
                    durationMs = 0 // Will be populated when audio metadata is parsed
                )
                fxRepository.upsert(track)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to import FX track"
            }
        }
    }

    fun updateFxTrack(id: Long, name: String, tags: List<String>) {
        viewModelScope.launch {
            try {
                val existingTrack = fxRepository.getById(id)
                if (existingTrack != null) {
                    fxRepository.upsert(
                        existingTrack.copy(
                            name = name,
                            tags = tags
                        )
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update FX track"
            }
        }
    }

    fun deleteFxTrack(id: Long) {
        viewModelScope.launch {
            try {
                fxRepository.delete(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete FX track"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
