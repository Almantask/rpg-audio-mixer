package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FeaturedFxTrack
import com.example.rpgaudiomixer.domain.model.FeaturedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class HomeDashboardContent(
    val activeCampaign: Campaign?,
    val resumeScene: Scene?,
    val topAtmosphere: FeaturedSoundscapeTrack?,
    val legendaryAction: FeaturedFxTrack?,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeDashboardContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeDashboardContent>> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDashboard() {
        viewModelScope.launch {
            val activeCampaignFlow = campaignRepository.observeActiveCampaign()
            combine(
                activeCampaignFlow,
                activeCampaignFlow.flatMapLatest { campaign ->
                    if (campaign == null) {
                        flowOf(null)
                    } else {
                        sessionRepository.observeResumeScene(campaign.id)
                    }
                },
                soundscapeRepository.observeTopPlayedTrack(),
                fxRepository.observeLegendaryAction(),
            ) { activeCampaign, resumeScene, topAtmosphere, legendaryAction ->
                HomeDashboardContent(
                    activeCampaign = activeCampaign,
                    resumeScene = resumeScene,
                    topAtmosphere = topAtmosphere,
                    legendaryAction = legendaryAction,
                )
            }.catch { throwable ->
                _uiState.value = UiState.Error(throwable.message ?: "Unable to load home dashboard.")
            }.collect { dashboard ->
                _uiState.value = UiState.Success(dashboard)
            }
        }
    }
}
