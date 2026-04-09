package com.example.rpgaudiomixer.app.ui.sessions

import com.example.rpgaudiomixer.domain.session.Session

sealed class SessionsUiState {
    object Loading : SessionsUiState()
    data class Success(val sessions: List<Session>) : SessionsUiState()
    data class Error(val message: String) : SessionsUiState()
}
