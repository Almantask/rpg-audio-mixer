package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Sessions screen.
 */
sealed class SessionsUiState {
    object Loading : SessionsUiState()
    data class Success(val sessions: List<Session>) : SessionsUiState()
    data class Error(val message: String) : SessionsUiState()
}

/**
 * ViewModel for Campaign Sessions screen.
 *
 * Manages session CRUD operations within a specific campaign.
 */
@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val campaignId: Long = checkNotNull(savedStateHandle["campaignId"])

    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            sessionRepository.observeByCampaign(campaignId)
                .catch { e ->
                    _uiState.value = SessionsUiState.Error(
                        e.message ?: "Failed to load sessions"
                    )
                }
                .collect { sessions ->
                    _uiState.value = SessionsUiState.Success(sessions)
                }
        }
    }

    /**
     * Create a new session.
     */
    fun createSession(name: String, date: Long, coverArtUri: String? = null) {
        viewModelScope.launch {
            try {
                sessionRepository.create(campaignId, name, date, coverArtUri)
            } catch (e: Exception) {
                _uiState.value = SessionsUiState.Error(
                    e.message ?: "Failed to create session"
                )
            }
        }
    }

    /**
     * Delete a session by ID.
     */
    fun deleteSession(id: Long) {
        viewModelScope.launch {
            try {
                sessionRepository.delete(id)
            } catch (e: Exception) {
                _uiState.value = SessionsUiState.Error(
                    e.message ?: "Failed to delete session"
                )
            }
        }
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        if (_uiState.value is SessionsUiState.Error) {
            loadSessions()
        }
    }
}
