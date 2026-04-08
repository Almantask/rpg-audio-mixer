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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val sessionSceneRepository: SessionSceneRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = savedStateHandle.get<String>("sessionId")
        ?: error("sessionId is required")

    private val _uiState = MutableStateFlow<SessionScenesUiState>(SessionScenesUiState.Loading)
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    init {
        loadSessionScenes()
    }

    private fun loadSessionScenes() {
        viewModelScope.launch {
            try {
                // Get scene IDs linked to this session
                val linkedSceneIds = sessionSceneRepository.getScenesBySession(sessionId)

                // Get full scene objects for linked scenes
                val linkedScenes = linkedSceneIds.mapNotNull { sceneId ->
                    sceneRepository.getSceneById(sceneId)
                }

                // Get all scenes to determine available scenes
                val allScenes = sceneRepository.getAllScenes()
                val availableScenes = allScenes.filter { scene ->
                    !linkedSceneIds.contains(scene.id)
                }

                _uiState.value = SessionScenesUiState.Success(
                    linkedScenes = linkedScenes,
                    availableScenes = availableScenes
                )
            } catch (e: Exception) {
                _uiState.value = SessionScenesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun linkScene(sceneId: String) {
        viewModelScope.launch {
            try {
                sessionSceneRepository.linkSceneToSession(sessionId, sceneId)
                loadSessionScenes()
                hideImportDialog()
            } catch (e: Exception) {
                _uiState.value = SessionScenesUiState.Error(e.message ?: "Failed to link scene")
            }
        }
    }

    fun unlinkScene(sceneId: String) {
        viewModelScope.launch {
            try {
                sessionSceneRepository.unlinkSceneFromSession(sessionId, sceneId)
                loadSessionScenes()
            } catch (e: Exception) {
                _uiState.value = SessionScenesUiState.Error(e.message ?: "Failed to unlink scene")
            }
        }
    }

    fun showImportDialog() {
        _showImportDialog.value = true
    }

    fun hideImportDialog() {
        _showImportDialog.value = false
    }
}
