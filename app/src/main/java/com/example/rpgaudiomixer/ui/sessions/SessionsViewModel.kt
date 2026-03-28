package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.storage.CampaignRepository
import com.example.rpgaudiomixer.domain.storage.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionsUiState(
    val campaign: Campaign? = null,
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    private var campaignId: Long = 0L

    fun init(campaignId: Long) {
        if (this.campaignId == campaignId) return
        this.campaignId = campaignId
    }

    fun uiState(campaignId: Long): StateFlow<SessionsUiState> = combine(
        campaignRepository.getCampaignById(campaignId),
        sessionRepository.getSessionsByCampaign(campaignId),
    ) { campaign, sessions ->
        SessionsUiState(
            campaign = campaign,
            sessions = sessions.sortedByDescending { it.playedAt },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionsUiState(),
    )

    fun addSession(name: String, description: String) {
        viewModelScope.launch {
            sessionRepository.insert(
                Session(campaignId = campaignId, name = name, description = description),
            )
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            sessionRepository.delete(session)
        }
    }
}
