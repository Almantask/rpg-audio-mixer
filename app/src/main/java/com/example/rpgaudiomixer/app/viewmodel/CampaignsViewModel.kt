package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaignsUiState(
    val campaigns: List<Campaign> = emptyList(),
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampaignsUiState())
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.getAllCampaigns().collect { campaigns ->
                _uiState.value = _uiState.value.copy(campaigns = campaigns)
            }
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun dismissCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun createCampaign(name: String, coverArtUri: String?) {
        viewModelScope.launch {
            campaignRepository.upsertCampaign(
                Campaign(name = name, coverArtUri = coverArtUri)
            )
            dismissCreateDialog()
        }
    }

    fun updateCampaign(campaign: Campaign) {
        viewModelScope.launch {
            campaignRepository.upsertCampaign(campaign)
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            campaignRepository.deleteCampaign(id)
        }
    }

    fun onCampaignResumed(id: Long) {
        viewModelScope.launch {
            campaignRepository.touchLastPlayed(id)
        }
    }
}
