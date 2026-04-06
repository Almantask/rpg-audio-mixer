package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: SceneRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScenesUiState>(ScenesUiState.Loading)
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll()
                .catch { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
                .collect { scenes -> _uiState.value = ScenesUiState.Success(scenes) }
        }
    }

    fun createScene(name: String, description: String?, tags: List<String>) {
        viewModelScope.launch {
            runCatching { repository.create(name, description, tags) }
                .onFailure { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
                .onFailure { e -> _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error") }
        }
    }
}
