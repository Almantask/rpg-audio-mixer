package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
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

    init {
        loadScenes()
    }

    private fun loadScenes() {
        viewModelScope.launch {
            try {
                sceneRepository.observeAll().collect { scenes ->
                    _uiState.value = UiState.Success(scenes)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load scenes")
            }
        }
    }

    fun createScene(name: String, description: String? = null, tags: String = "") {
        viewModelScope.launch {
            try {
                sceneRepository.create(
                    name = name,
                    description = description,
                    tags = tags
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create scene")
            }
        }
    }

    fun deleteScene(sceneId: Long) {
        viewModelScope.launch {
            try {
                sceneRepository.delete(sceneId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete scene")
            }
        }
    }

    fun updateScene(sceneId: Long, name: String, description: String?, tags: String) {
        viewModelScope.launch {
            try {
                sceneRepository.update(
                    id = sceneId,
                    name = name,
                    description = description,
                    tags = tags
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update scene")
            }
        }
    }
}
