package com.example.rpgaudiomixer.app.ui.home

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.Scene

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: Scene? = null,
    val topAtmosphere: SoundscapeTrack? = null,
    val legendaryAction: FxTrack? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
