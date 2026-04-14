package com.example.rpgaudiomixer.app.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrashUiState(
    val deletedCampaigns: List<Campaign> = emptyList(),
    val deletedSessions: List<Session> = emptyList(),
    val deletedScenes: List<Scene> = emptyList(),
) {
    val isEmpty: Boolean
        get() = deletedCampaigns.isEmpty() && deletedSessions.isEmpty() && deletedScenes.isEmpty()
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    val uiState: StateFlow<TrashUiState> = combine(
        campaignRepository.observeDeleted(),
        sessionRepository.observeDeleted(),
        sceneRepository.observeDeleted(),
    ) { campaigns, sessions, scenes ->
        TrashUiState(
            deletedCampaigns = campaigns,
            deletedSessions = sessions,
            deletedScenes = scenes,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrashUiState(),
    )

    fun restoreCampaign(id: Long) {
        viewModelScope.launch {
            campaignRepository.restoreCampaign(id)
            sessionRepository.restoreByCampaign(id)
        }
    }

    fun permanentlyDeleteCampaign(id: Long) {
        viewModelScope.launch {
            campaignRepository.permanentlyDeleteCampaign(id)
        }
    }

    fun restoreSession(id: Long) {
        viewModelScope.launch {
            sessionRepository.restore(id)
        }
    }

    fun permanentlyDeleteSession(id: Long) {
        viewModelScope.launch {
            sessionRepository.permanentlyDelete(id)
        }
    }

    fun restoreScene(id: Long) {
        viewModelScope.launch {
            sceneRepository.restoreScene(id)
        }
    }

    fun permanentlyDeleteScene(id: Long) {
        viewModelScope.launch {
            sceneRepository.permanentlyDeleteScene(id)
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            val state = uiState.value
            state.deletedCampaigns.forEach { campaignRepository.permanentlyDeleteCampaign(it.id) }
            state.deletedSessions.forEach { sessionRepository.permanentlyDelete(it.id) }
            state.deletedScenes.forEach { sceneRepository.permanentlyDeleteScene(it.id) }
        }
    }
}
