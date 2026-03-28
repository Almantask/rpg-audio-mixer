package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import com.example.rpgaudiomixer.domain.storage.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionScenesUiState(
    val session: Session? = null,
    val scenes: List<Scene> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    fun uiState(sessionId: Long): StateFlow<SessionScenesUiState> = combine(
        sessionRepository.getSessionById(sessionId),
        sceneRepository.getScenesBySession(sessionId),
    ) { session, scenes ->
        SessionScenesUiState(
            session = session,
            scenes = scenes.sortedBy { it.name },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionScenesUiState(),
    )

    fun importScene(sessionId: Long, name: String, description: String) {
        viewModelScope.launch {
            val sceneId = sceneRepository.insert(
                Scene(name = name, description = description),
            )
            sceneRepository.addSceneToSession(sessionId, sceneId)
        }
    }

    fun removeSceneFromSession(sessionId: Long, scene: Scene) {
        viewModelScope.launch {
            sceneRepository.removeSceneFromSession(sessionId, scene.id)
        }
    }
}
