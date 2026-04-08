package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val campaignId: String = savedStateHandle.get<String>("campaignId")
        ?: error("campaignId is required")

    private val _uiState = MutableStateFlow<SessionsUiState>(SessionsUiState.Loading)
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            try {
                val sessions = sessionRepository.getSessionsByCampaign(campaignId)
                // Sessions are already sorted by date desc from repository
                _uiState.value = SessionsUiState.Success(sessions)
            } catch (e: Exception) {
                _uiState.value = SessionsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createSession(name: String) {
        viewModelScope.launch {
            try {
                sessionRepository.createSession(campaignId, name)
                loadSessions()
                hideCreateDialog()
            } catch (e: Exception) {
                _uiState.value = SessionsUiState.Error(e.message ?: "Failed to create session")
            }
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(id)
                loadSessions()
            } catch (e: Exception) {
                _uiState.value = SessionsUiState.Error(e.message ?: "Failed to delete session")
            }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }
}
