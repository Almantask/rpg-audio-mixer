package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.TrackStats

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val activeCampaign: Campaign?,
        val lastScene: Scene?,
        val topAtmosphere: TrackStats?,
        val legendaryAction: TrackStats?
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
