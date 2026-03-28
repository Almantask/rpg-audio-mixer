package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.storage.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaignsUiState(
    val campaigns: List<Campaign> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    val uiState: StateFlow<CampaignsUiState> = campaignRepository.getAllCampaigns()
        .map { campaigns ->
            CampaignsUiState(
                campaigns = campaigns.sortedByDescending { it.lastPlayedAt },
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CampaignsUiState(),
        )

    fun addCampaign(name: String, description: String) {
        viewModelScope.launch {
            campaignRepository.insert(
                Campaign(name = name, description = description),
            )
        }
    }

    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            campaignRepository.delete(campaign)
        }
    }
}
