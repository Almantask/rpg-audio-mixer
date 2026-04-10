package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle[MainNavDestination.SESSION_ID_ARG])

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<Scene>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Scene>>> = _uiState.asStateFlow()

    private val _availableScenes = MutableStateFlow<List<Scene>>(emptyList())
    val availableScenes: StateFlow<List<Scene>> = _availableScenes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSession()
        observeLinkedScenes()
        observeAvailableScenes()
    }

    fun importScenes(sceneIds: List<Long>) {
        if (sceneIds.isEmpty()) {
            _errorMessage.value = "Select at least one scene to import."
            return
        }

        viewModelScope.launch {
            runCatching {
                sessionRepository.linkScenes(sessionId, sceneIds)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to import scenes."
            }
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch {
            runCatching {
                sessionRepository.unlinkScene(sessionId, sceneId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to unlink scene."
            }
        }
    }

    fun openScene(sceneId: Long) {
        viewModelScope.launch {
            runCatching {
                sessionRepository.updateLastOpenedScene(sessionId, sceneId)
                _session.value?.let { loadedSession ->
                    campaignRepository.touchCampaign(loadedSession.campaignId)
                }
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to save recent scene."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun loadSession() {
        viewModelScope.launch {
            _session.value = sessionRepository.getSession(sessionId)
        }
    }

    private fun observeLinkedScenes() {
        viewModelScope.launch {
            sessionRepository.observeScenesBySession(sessionId)
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load linked scenes.",
                    )
                }
                .collect { scenes ->
                    _uiState.value = UiState.Success(scenes)
                }
        }
    }

    private fun observeAvailableScenes() {
        viewModelScope.launch {
            combine(
                sceneRepository.observeAll(),
                sessionRepository.observeLinkedSceneIds(sessionId),
            ) { scenes, linkedSceneIds ->
                scenes.filterNot { scene -> scene.id in linkedSceneIds }
            }.collect { availableScenes ->
                _availableScenes.value = availableScenes
            }
        }
    }
}
