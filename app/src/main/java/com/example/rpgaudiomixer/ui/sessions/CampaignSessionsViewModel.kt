package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val repository: SessionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _campaignId: Long = savedStateHandle["campaignId"] ?: 0L

    constructor(
        repository: SessionRepository,
        campaignId: Long,
    ) : this(
        repository = repository,
        savedStateHandle = SavedStateHandle(mapOf("campaignId" to campaignId)),
    )

    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeByCampaign(_campaignId)
                .catch { e -> _uiState.value = SessionsUiState.Error(e.message ?: "Unknown error") }
                .collect { sessions -> _uiState.value = SessionsUiState.Success(sessions) }
        }
    }

    fun createSession(name: String, coverArtUri: String?) {
        viewModelScope.launch {
            runCatching { repository.create(_campaignId, name, coverArtUri) }
                .onFailure { e -> _uiState.value = SessionsUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
                .onFailure { e -> _uiState.value = SessionsUiState.Error(e.message ?: "Unknown error") }
        }
    }
}
