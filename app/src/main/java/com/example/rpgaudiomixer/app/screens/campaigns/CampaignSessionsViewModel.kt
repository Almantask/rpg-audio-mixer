package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    private val repository: SessionRepository,
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _campaignId = MutableStateFlow<Long>(-1)
    
    val sessions: StateFlow<List<Session>> = _campaignId
        .flatMapLatest { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    campaignRepository.updateLastPlayed(id)
                }
            }
            repository.observeByCampaign(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCampaignId(id: Long) {
        _campaignId.value = id
    }

    fun createSession(name: String, coverUri: String?) {
        viewModelScope.launch {
            repository.upsert(
                Session(
                    campaignId = _campaignId.value,
                    name = name,
                    coverArtUri = coverUri
                )
            )
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }
}
