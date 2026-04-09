package com.example.rpgaudiomixer.app.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.session.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val repository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val campaignId: Long = checkNotNull(savedStateHandle["campaignId"])

    val uiState: StateFlow<SessionsUiState> = repository.observeByCampaign(campaignId)
        .map { SessionsUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionsUiState.Loading
        )

    fun createSession(name: String, coverUri: String? = null) {
        viewModelScope.launch {
            val session = Session(
                campaignId = campaignId,
                name = name,
                date = System.currentTimeMillis(),
                coverArtUri = coverUri
            )
            repository.upsert(session)
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
