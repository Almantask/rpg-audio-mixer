package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionScenesUiState(
    val linkedScenes: List<Scene> = emptyList(),
    val availableScenes: List<Scene> = emptyList()
)

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sessionSceneRepository: SessionSceneRepository,
    private val sceneRepository: SceneRepository
) : ViewModel() {

    private val _sessionId = MutableStateFlow<Long?>(null)
    private val _uiState = MutableStateFlow<UiState<SessionScenesUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<SessionScenesUiState>> = _uiState.asStateFlow()

    fun loadSession(sessionId: Long) {
        _sessionId.value = sessionId

        viewModelScope.launch {
            try {
                combine(
                    sessionSceneRepository.observeScenesBySession(sessionId),
                    sceneRepository.observeAll()
                ) { linkedScenes, allScenes ->
                    val linkedSceneIds = linkedScenes.map { it.id }.toSet()
                    val availableScenes = allScenes.filterNot { it.id in linkedSceneIds }
                    SessionScenesUiState(
                        linkedScenes = linkedScenes,
                        availableScenes = availableScenes
                    )
                }.collect { state ->
                    _uiState.value = UiState.Success(state)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load session scenes")
            }
        }
    }

    fun linkScenes(sceneIds: List<Long>) {
        viewModelScope.launch {
            val sessionId = _sessionId.value ?: return@launch
            try {
                sceneIds.forEach { sceneId ->
                    sessionSceneRepository.linkScene(sessionId, sceneId)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to link scenes")
            }
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            val sessionId = _sessionId.value ?: return@launch
            try {
                sessionSceneRepository.unlinkScene(sessionId, sceneId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to unlink scene")
            }
        }
    }
}
