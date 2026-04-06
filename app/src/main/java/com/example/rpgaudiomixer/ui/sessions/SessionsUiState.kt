package com.example.rpgaudiomixer.ui.sessions

import com.example.rpgaudiomixer.domain.model.Session

sealed interface SessionsUiState {
    data object Loading : SessionsUiState
    data class Success(val sessions: List<Session>) : SessionsUiState
    data class Error(val message: String) : SessionsUiState
}
