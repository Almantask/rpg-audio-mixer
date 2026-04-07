package com.example.rpgaudiomixer.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val campaignId: Long = savedStateHandle.get<String>("campaignId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<UiState<List<Session>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Session>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            try {
                sessionRepository.observeByCampaign(campaignId)
                    .catch { error ->
                        _uiState.value = UiState.Error(error.message ?: "Unknown error")
                    }
                    .collect { sessions ->
                        _uiState.value = UiState.Success(sessions)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createSession(name: String, coverUri: String?) {
        viewModelScope.launch {
            try {
                sessionRepository.create(campaignId, name, coverUri)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create session"
            }
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            try {
                sessionRepository.delete(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete session"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
