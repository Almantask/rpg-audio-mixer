package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionScenesUiState {
    data object Loading : SessionScenesUiState
    data class Success(val linkedScenes: List<Scene>, val allScenes: List<Scene>) : SessionScenesUiState
    data class Error(val message: String) : SessionScenesUiState
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    val uiState: StateFlow<SessionScenesUiState> = combine(
        sceneRepository.observeBySession(sessionId),
        sceneRepository.observeAll()
    ) { linked, all -> SessionScenesUiState.Success(linked, all) as SessionScenesUiState }
        .catch { emit(SessionScenesUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionScenesUiState.Loading)

    fun linkScene(sceneId: Long) {
        viewModelScope.launch { sceneRepository.linkToSession(sceneId, sessionId) }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch { sceneRepository.unlinkFromSession(sceneId, sessionId) }
    }
}
