package com.example.rpgaudiomixer.app.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    val uiState: StateFlow<SessionScenesUiState> = combine(
        sessionRepository.observeScenesBySession(sessionId),
        sceneRepository.observeAll()
    ) { linked, all ->
        SessionScenesUiState.Success(linked, all)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionScenesUiState.Loading
    )

    fun linkScene(sceneId: Long) {
        viewModelScope.launch {
            sessionRepository.linkScene(sessionId, sceneId)
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            sessionRepository.unlinkScene(sessionId, sceneId)
        }
    }
}
