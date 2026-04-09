package com.example.rpgaudiomixer.app.ui.campaigns

import com.example.rpgaudiomixer.domain.campaign.Campaign

sealed class CampaignsUiState {
    object Loading : CampaignsUiState()
    data class Success(
        val campaigns: List<Campaign> = emptyList()
    ) : CampaignsUiState()
    data class Error(val message: String) : CampaignsUiState()
}
