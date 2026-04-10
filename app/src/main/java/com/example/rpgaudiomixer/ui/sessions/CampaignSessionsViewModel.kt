package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface CampaignSessionsUiState {
    data object Loading : CampaignSessionsUiState
    data class Success(
        val campaign: Campaign?,
        val sessions: List<Session>,
    ) : CampaignSessionsUiState

    data class Error(val message: String) : CampaignSessionsUiState
}

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val campaignId: Long = checkNotNull(savedStateHandle["campaignId"])
    private val _uiState = MutableStateFlow<CampaignSessionsUiState>(CampaignSessionsUiState.Loading)
    val uiState: StateFlow<CampaignSessionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                campaignRepository.observeCampaign(campaignId),
                sessionRepository.observeSessionsByCampaign(campaignId),
            ) { campaign, sessions ->
                CampaignSessionsUiState.Success(campaign = campaign, sessions = sessions)
            }
                .catch { throwable ->
                    _uiState.value = CampaignSessionsUiState.Error(
                        throwable.message ?: "Unable to load sessions.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun createSession(
        name: String,
        dateMillis: Long,
        coverArtUri: String?,
    ) {
        if (name.trim().isBlank()) {
            return
        }

        viewModelScope.launch {
            sessionRepository.createSession(
                campaignId = campaignId,
                name = name.trim(),
                dateMillis = dateMillis,
                coverArtUri = coverArtUri,
            )
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }
}
