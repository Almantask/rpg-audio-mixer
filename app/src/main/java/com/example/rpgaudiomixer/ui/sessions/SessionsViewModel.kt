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

sealed interface SessionsUiState {
    data object Loading : SessionsUiState
    data class Success(val sessions: List<Session>) : SessionsUiState
    data class Error(val message: String) : SessionsUiState
}

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val campaignId: Long = savedStateHandle.get<String>("campaignId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createSession(name: String, coverArtUri: String? = null) {
        viewModelScope.launch {
            try {
                sessionRepository.create(campaignId, name, coverArtUri)
                hideCreateDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create session"
            }
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            try {
                sessionRepository.delete(session)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete session"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
