package com.example.rpgaudiomixer.ui.campaignsessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val campaignId: Long = checkNotNull(savedStateHandle[MainNavDestination.CAMPAIGN_ID_ARG])

    private val _campaign = MutableStateFlow<Campaign?>(null)
    val campaign: StateFlow<Campaign?> = _campaign.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<Session>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Session>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCampaign()
        observeSessions()
    }

    fun createSession(
        name: String,
        dateMillis: Long?,
        coverArtUri: String?,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _errorMessage.value = "Session name is required."
            return
        }
        if (dateMillis == null) {
            _errorMessage.value = "Session date is required."
            return
        }

        viewModelScope.launch {
            runCatching {
                sessionRepository.createSession(
                    campaignId = campaignId,
                    name = trimmedName,
                    dateMillis = dateMillis,
                    coverArtUri = coverArtUri,
                )
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to create session."
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            runCatching {
                sessionRepository.deleteSession(sessionId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to delete session."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun loadCampaign() {
        viewModelScope.launch {
            _campaign.value = campaignRepository.getCampaign(campaignId)
        }
    }

    private fun observeSessions() {
        viewModelScope.launch {
            sessionRepository.observeByCampaign(campaignId)
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load sessions.",
                    )
                }
                .collect { sessions ->
                    _uiState.value = UiState.Success(sessions)
                }
        }
    }
}
