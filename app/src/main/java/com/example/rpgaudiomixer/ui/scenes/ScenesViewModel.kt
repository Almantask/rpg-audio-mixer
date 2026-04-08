package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScenesUiState>(ScenesUiState.Loading)
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            try {
                val scenes = sceneRepository.getAllScenes()
                _uiState.value = ScenesUiState.Success(scenes)
            } catch (e: Exception) {
                _uiState.value = ScenesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createScene(name: String, description: String? = null, tags: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                sceneRepository.createScene(name, description, tags)
                loadScenes()
                hideCreateDialog()
            } catch (e: Exception) {
                _uiState.value = ScenesUiState.Error(e.message ?: "Failed to create scene")
            }
        }
    }

    fun deleteScene(id: String) {
        viewModelScope.launch {
            try {
                sceneRepository.deleteScene(id)
                loadScenes()
            } catch (e: Exception) {
                _uiState.value = ScenesUiState.Error(e.message ?: "Failed to delete scene")
            }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }
}
