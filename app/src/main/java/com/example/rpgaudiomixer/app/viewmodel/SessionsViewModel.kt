package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionsUiState(
    val campaignName: String = "",
    val sessions: List<Session> = emptyList(),
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class SessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    val campaignId: Long = checkNotNull(savedStateHandle["campaignId"])

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.getCampaignById(campaignId)?.let { campaign ->
                _uiState.value = _uiState.value.copy(campaignName = campaign.name)
            }
        }
        viewModelScope.launch {
            sessionRepository.getSessionsForCampaign(campaignId).collect { sessions ->
                _uiState.value = _uiState.value.copy(sessions = sessions)
            }
        }
    }

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun dismissCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }

    fun createSession(name: String, coverArtUri: String?) {
        viewModelScope.launch {
            sessionRepository.upsertSession(
                Session(campaignId = campaignId, name = name, coverArtUri = coverArtUri)
            )
            dismissCreateDialog()
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch { sessionRepository.deleteSession(id) }
    }
}
