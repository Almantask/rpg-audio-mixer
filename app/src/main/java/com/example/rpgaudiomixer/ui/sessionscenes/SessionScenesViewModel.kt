package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SessionScenesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionScenesUiState {
    data object Loading : SessionScenesUiState
    data class Success(val scenes: List<Scene>) : SessionScenesUiState
    data class Error(val message: String) : SessionScenesUiState
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    private val sessionScenesRepository: SessionScenesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionScenesUiState>(SessionScenesUiState.Loading)
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    fun loadScenes(sessionId: Long) {
        viewModelScope.launch {
            sessionScenesRepository.observeBySession(sessionId)
                .catch { e -> _uiState.value = SessionScenesUiState.Error(e.message ?: "Unknown error") }
                .collectLatest { scenes ->
                    _uiState.value = SessionScenesUiState.Success(scenes)
                }
        }
    }
}
