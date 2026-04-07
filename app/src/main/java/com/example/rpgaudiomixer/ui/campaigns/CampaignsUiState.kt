package com.example.rpgaudiomixer.ui.campaigns

import com.example.rpgaudiomixer.domain.model.Campaign

sealed interface CampaignsUiState {
    data object Loading : CampaignsUiState
    data class Success(val campaigns: List<Campaign>) : CampaignsUiState
    data class Error(val message: String) : CampaignsUiState
}
