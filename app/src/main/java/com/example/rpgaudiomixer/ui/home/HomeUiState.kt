package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.model.Campaign

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object NoCampaigns : HomeUiState
    data class Success(
        val activeCampaign: Campaign,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
