package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<SoundscapeCategory>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<SoundscapeCategory>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            soundscapeRepository.observeCategories()
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load soundscape categories.",
                    )
                }
                .collect { categories ->
                    _uiState.value = UiState.Success(categories)
                }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            runCatching {
                soundscapeRepository.deleteCategory(categoryId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to delete soundscape category."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}
