package com.example.rpgaudiomixer.app.screens.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionsUiState {
    data object Loading : SessionsUiState
    data class Success(val sessions: List<Session>) : SessionsUiState
    data class Error(val message: String) : SessionsUiState
}

@HiltViewModel
class SessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SessionRepository
) : ViewModel() {

    private val campaignId: Long = checkNotNull(savedStateHandle["campaignId"])

    val uiState: StateFlow<SessionsUiState> = repository.observeByCampaign(campaignId)
        .map<List<Session>, SessionsUiState> { SessionsUiState.Success(it) }
        .catch { emit(SessionsUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionsUiState.Loading)

    fun createSession(name: String) {
        viewModelScope.launch {
            repository.createSession(campaignId, name)
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
