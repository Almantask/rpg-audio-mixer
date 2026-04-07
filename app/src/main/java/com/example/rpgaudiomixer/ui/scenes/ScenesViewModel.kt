package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Scene>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Scene>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            try {
                sceneRepository.observeAll()
                    .catch { error ->
                        _uiState.value = UiState.Error(error.message ?: "Unknown error")
                    }
                    .collect { scenes ->
                        _uiState.value = UiState.Success(scenes)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createScene(name: String, description: String?, tags: List<String>) {
        viewModelScope.launch {
            try {
                sceneRepository.create(name, description, tags)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create scene"
            }
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch {
            try {
                sceneRepository.delete(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete scene"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
