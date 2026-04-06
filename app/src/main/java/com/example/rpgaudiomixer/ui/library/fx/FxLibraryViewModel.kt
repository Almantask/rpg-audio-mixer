package com.example.rpgaudiomixer.ui.library.fx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.fx.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val repository: FxRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _uiState = MutableStateFlow<FxLibraryUiState>(FxLibraryUiState.Loading)
    val uiState: StateFlow<FxLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.observeAll() else repository.search(query)
                }
                .catch { e -> _uiState.value = FxLibraryUiState.Error(e.message ?: "Unknown error") }
                .collect { tracks ->
                    _uiState.value = FxLibraryUiState.Success(tracks, _searchQuery.value)
                }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun deleteTrack(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
                .onFailure { e -> _uiState.value = FxLibraryUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun updateTrack(track: com.example.rpgaudiomixer.domain.model.FxTrack) {
        viewModelScope.launch {
            runCatching { repository.update(track) }
                .onFailure { e -> _uiState.value = FxLibraryUiState.Error(e.message ?: "Unknown error") }
        }
    }
}
