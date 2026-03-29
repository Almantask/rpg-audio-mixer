package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScenesUiState {
    data object Loading : ScenesUiState
    data class Success(val scenes: List<Scene>) : ScenesUiState
    data class Error(val message: String) : ScenesUiState
}

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScenesUiState>(ScenesUiState.Loading)
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sceneRepository.observeAll()
                .catch { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
                .collectLatest { scenes ->
                    _uiState.value = ScenesUiState.Success(scenes)
                }
        }
    }
}
