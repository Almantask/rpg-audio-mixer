package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CampaignsUiState {
    data object Loading : CampaignsUiState
    data class Success(val campaigns: List<Campaign>) : CampaignsUiState
    data class Error(val message: String) : CampaignsUiState
}

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {

    val uiState: StateFlow<CampaignsUiState> = repository.observeAll()
        .map<List<Campaign>, CampaignsUiState> { CampaignsUiState.Success(it) }
        .catch { emit(CampaignsUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CampaignsUiState.Loading)

    fun createCampaign(name: String, coverArtUri: String? = null) {
        viewModelScope.launch {
            repository.createCampaign(name, coverArtUri)
        }
    }

    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            repository.deleteCampaign(campaign)
        }
    }
}
