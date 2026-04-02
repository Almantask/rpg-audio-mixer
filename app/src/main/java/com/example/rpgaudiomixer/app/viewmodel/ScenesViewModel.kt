package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScenesUiState(
    val scenes: List<Scene> = emptyList(),
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenesUiState())
    val uiState: StateFlow<ScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sceneRepository.getAllScenes().collect { scenes ->
                _uiState.value = _uiState.value.copy(scenes = scenes)
            }
        }
    }

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun dismissCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }

    fun createScene(name: String, tags: List<String>) {
        viewModelScope.launch {
            sceneRepository.upsertScene(Scene(name = name, tags = tags))
            dismissCreateDialog()
        }
    }

    fun deleteScene(id: Long) {
        viewModelScope.launch { sceneRepository.deleteScene(id) }
    }
}
