package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionScenesUiState(
    val sessionName: String = "",
    val scenes: List<Scene> = emptyList(),
    val showImportSheet: Boolean = false,
    val allScenes: List<Scene> = emptyList(),
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(SessionScenesUiState())
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.getSessionById(sessionId)?.let { session ->
                _uiState.value = _uiState.value.copy(sessionName = session.name)
            }
        }
        viewModelScope.launch {
            sceneRepository.getScenesForSession(sessionId).collect { scenes ->
                _uiState.value = _uiState.value.copy(scenes = scenes)
            }
        }
    }

    fun showImportSheet() {
        viewModelScope.launch {
            sceneRepository.getAllScenes().collect { all ->
                _uiState.value = _uiState.value.copy(allScenes = all, showImportSheet = true)
            }
        }
    }

    fun dismissImportSheet() { _uiState.value = _uiState.value.copy(showImportSheet = false) }

    fun importScene(sceneId: Long) {
        viewModelScope.launch {
            sceneRepository.addSceneToSession(sessionId, sceneId)
            dismissImportSheet()
        }
    }

    fun removeScene(sceneId: Long) {
        viewModelScope.launch {
            sceneRepository.removeSceneFromSession(sessionId, sceneId)
        }
    }

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun dismissCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }

    fun createAndAddScene(name: String) {
        viewModelScope.launch {
            val sceneId = sceneRepository.upsertScene(Scene(name = name))
            sceneRepository.addSceneToSession(sessionId, sceneId)
            dismissCreateDialog()
        }
    }
}
