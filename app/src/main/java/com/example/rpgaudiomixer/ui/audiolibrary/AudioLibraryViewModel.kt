package com.example.rpgaudiomixer.ui.audiolibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.FX
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import com.example.rpgaudiomixer.domain.repository.FXRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AudioLibraryUiState {
    data object Loading : AudioLibraryUiState
    data class Success(val soundscapes: List<SoundscapeCategory>, val fx: List<FX>) : AudioLibraryUiState
    data class Error(val message: String) : AudioLibraryUiState
}

@HiltViewModel
class AudioLibraryViewModel @Inject constructor(
    private val soundscapeCategoryRepository: SoundscapeCategoryRepository,
    private val fxRepository: FXRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AudioLibraryUiState>(AudioLibraryUiState.Loading)
    val uiState: StateFlow<AudioLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                soundscapeCategoryRepository.observeAll().collectLatest { soundscapes ->
                    fxRepository.observeAll().collectLatest { fx ->
                        _uiState.value = AudioLibraryUiState.Success(soundscapes, fx)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AudioLibraryUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
