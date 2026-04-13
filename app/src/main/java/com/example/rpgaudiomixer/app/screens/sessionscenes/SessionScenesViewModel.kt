package com.example.rpgaudiomixer.app.screens.sessionscenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionScenesUiState {
    data object Loading : SessionScenesUiState
    data class Success(val scenes: List<Scene>) : SessionScenesUiState
    data class Error(val message: String) : SessionScenesUiState
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val repository: SceneRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    val uiState: StateFlow<SessionScenesUiState> = repository.observeScenesForSession(sessionId)
        .map<List<Scene>, SessionScenesUiState> { SessionScenesUiState.Success(it) }
        .catch { emit(SessionScenesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionScenesUiState.Loading)

    val allScenes: StateFlow<List<Scene>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            repository.unlinkSceneFromSession(sessionId, sceneId)
        }
    }

    fun linkScenes(sceneIds: List<Long>) {
        viewModelScope.launch {
            sceneIds.forEach { sceneId ->
                repository.linkSceneToSession(sessionId, sceneId)
            }
        }
    }
}
