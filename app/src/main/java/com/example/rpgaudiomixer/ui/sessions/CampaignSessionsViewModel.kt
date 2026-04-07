package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _campaignId = MutableStateFlow<Long?>(null)
    private val _uiState = MutableStateFlow<UiState<List<Session>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Session>>> = _uiState.asStateFlow()

    fun loadSessions(campaignId: Long) {
        _campaignId.value = campaignId

        viewModelScope.launch {
            try {
                sessionRepository.observeByCampaign(campaignId).collect { sessions ->
                    _uiState.value = UiState.Success(sessions)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load sessions")
            }
        }
    }

    fun createSession(name: String, date: String, coverArtUri: String? = null) {
        viewModelScope.launch {
            val campaignId = _campaignId.value ?: return@launch
            try {
                sessionRepository.create(
                    campaignId = campaignId,
                    name = name,
                    date = date,
                    coverArtUri = coverArtUri
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create session")
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                sessionRepository.delete(sessionId)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete session")
            }
        }
    }
}
