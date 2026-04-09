package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.ResumeScene
import com.example.rpgaudiomixer.domain.model.SoundscapeHighlight
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: ResumeScene? = null,
    val topAtmosphere: SoundscapeHighlight? = null,
    val legendaryAction: FxTrack? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    campaignRepository: CampaignRepository,
    sessionRepository: SessionRepository,
    soundscapeRepository: SoundscapeRepository,
    fxRepository: FxRepository,
) : ViewModel() {
    private val activeCampaign = campaignRepository.observeMostRecentCampaign()

    private val resumeScene = activeCampaign.flatMapLatest { campaign ->
        if (campaign == null) {
            flowOf<ResumeScene?>(null)
        } else {
            sessionRepository.observeLastOpenedScene(campaign.id)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        activeCampaign,
        resumeScene,
        soundscapeRepository.observeMostPlayedTrack(),
        fxRepository.observeMostPlayedTrack(),
    ) { campaign, scene, topAtmosphere, legendaryAction ->
        HomeUiState(
            activeCampaign = campaign,
            resumeScene = scene,
            topAtmosphere = topAtmosphere,
            legendaryAction = legendaryAction,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )
}
