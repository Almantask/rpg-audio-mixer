package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionScenesUiState {
    data object Loading : SessionScenesUiState
    data class Success(val scenes: List<Scene>) : SessionScenesUiState
    data class Error(val message: String) : SessionScenesUiState
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sessionSceneRepository: SessionSceneRepository,
    private val sceneRepository: SceneRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<SessionScenesUiState>(SessionScenesUiState.Loading)
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    private val _availableScenes = MutableStateFlow<List<Scene>>(emptyList())
    val availableScenes: StateFlow<List<Scene>> = _availableScenes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSessionScenes()
        loadAvailableScenes()
    }

    private fun loadSessionScenes() {
        viewModelScope.launch {
            sessionSceneRepository.observeScenesBySession(sessionId)
                .catch { e ->
                    _uiState.value = SessionScenesUiState.Error(
                        e.message ?: "Failed to load session scenes"
                    )
                }
                .collect { scenes ->
                    _uiState.value = SessionScenesUiState.Success(scenes)
                }
        }
    }

    private fun loadAvailableScenes() {
        viewModelScope.launch {
            sceneRepository.observeAll()
                .collect { scenes ->
                    _availableScenes.value = scenes
                }
        }
    }

    fun showImportDialog() {
        _showImportDialog.value = true
    }

    fun hideImportDialog() {
        _showImportDialog.value = false
    }

    fun importScenes(sceneIds: List<Long>) {
        viewModelScope.launch {
            try {
                sceneIds.forEach { sceneId ->
                    sessionSceneRepository.linkSceneToSession(sessionId, sceneId)
                }
                hideImportDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to import scenes"
            }
        }
    }

    fun unlinkScene(scene: Scene) {
        viewModelScope.launch {
            try {
                sessionSceneRepository.unlinkSceneFromSession(sessionId, scene.id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to unlink scene"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
