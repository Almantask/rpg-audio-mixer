package com.example.rpgaudiomixer.ui.library.soundscapes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val repository: SoundscapeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SoundscapeLibraryUiState>(SoundscapeLibraryUiState.Loading)
    val uiState: StateFlow<SoundscapeLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAllCategories()
                .catch { e -> _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Unknown error") }
                .collect { categories -> _uiState.value = SoundscapeLibraryUiState.Success(categories) }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            runCatching { repository.createCategory(name) }
                .onFailure { e -> _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            runCatching { repository.deleteCategory(id) }
                .onFailure { e -> _uiState.value = SoundscapeLibraryUiState.Error(e.message ?: "Unknown error") }
        }
    }
}
