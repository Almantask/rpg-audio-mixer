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

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            sceneRepository.observeAll()
                .catch { e ->
                    _uiState.value = ScenesUiState.Error(
                        e.message ?: "Failed to load scenes"
                    )
                }
                .collect { scenes ->
                    _uiState.value = ScenesUiState.Success(scenes)
                }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createScene(name: String, description: String? = null, tags: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                sceneRepository.create(name, description, tags)
                hideCreateDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create scene"
            }
        }
    }

    fun deleteScene(scene: Scene) {
        viewModelScope.launch {
            try {
                sceneRepository.delete(scene)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete scene"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
