package com.example.rpgaudiomixer.ui.composer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ComposerUiState {
    data object Loading : ComposerUiState
    data class Success(val category: SoundscapeCategory) : ComposerUiState
    data class Error(val message: String) : ComposerUiState
}

@HiltViewModel
class ComposerViewModel @Inject constructor(
    private val repository: SoundscapeCategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ComposerUiState>(ComposerUiState.Loading)
    val uiState: StateFlow<ComposerUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.observeAll()
                .catch { e -> _uiState.value = ComposerUiState.Error(e.message ?: "Unknown error") }
                .collectLatest { categories ->
                    val cat = categories.find { it.id == categoryId }
                    if (cat != null) {
                        _uiState.value = ComposerUiState.Success(cat)
                    } else {
                        _uiState.value = ComposerUiState.Error("Category not found")
                    }
                }
        }
    }

    // Add methods for adding/removing layers, saving, etc. as needed
}
