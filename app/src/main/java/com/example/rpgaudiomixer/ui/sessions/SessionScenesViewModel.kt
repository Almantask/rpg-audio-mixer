package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface SessionScenesUiState {
    data object Loading : SessionScenesUiState
    data class Success(
        val session: Session?,
        val linkedScenes: List<Scene>,
        val availableScenesToImport: List<Scene>,
    ) : SessionScenesUiState

    data class Error(val message: String) : SessionScenesUiState
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val campaignRepository: CampaignRepository,
) : ViewModel() {
    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])
    private val _uiState = MutableStateFlow<SessionScenesUiState>(SessionScenesUiState.Loading)
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    init {
        observeState()
    }

    constructor(
        sessionId: Long,
        sessionRepository: SessionRepository,
        sceneRepository: SceneRepository,
        campaignRepository: CampaignRepository,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf("sessionId" to sessionId)),
        sessionRepository = sessionRepository,
        sceneRepository = sceneRepository,
        campaignRepository = campaignRepository,
    )

    private fun observeState() {
        viewModelScope.launch {
            combine(
                sessionRepository.observeSession(sessionId),
                sessionRepository.observeScenesBySession(sessionId),
                sceneRepository.observeScenes(),
            ) { session, linkedScenes, allScenes ->
                val linkedIds = linkedScenes.map { it.id }.toSet()
                SessionScenesUiState.Success(
                    session = session,
                    linkedScenes = linkedScenes,
                    availableScenesToImport = allScenes.filterNot { scene -> scene.id in linkedIds },
                )
            }
                .catch { throwable ->
                    _uiState.value = SessionScenesUiState.Error(
                        throwable.message ?: "Unable to load session scenes.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun importScenes(sceneIds: List<Long>) {
        if (sceneIds.isEmpty()) {
            return
        }

        viewModelScope.launch {
            sessionRepository.linkScenes(sessionId, sceneIds)
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            sessionRepository.unlinkScene(sessionId, sceneId)
        }
    }

    fun onSceneOpened(sceneId: Long) {
        viewModelScope.launch {
            sessionRepository.markSceneOpened(sessionId = sessionId, sceneId = sceneId)
            val session = (uiState.value as? SessionScenesUiState.Success)?.session ?: return@launch
            campaignRepository.markCampaignPlayed(session.campaignId)
        }
    }
}
