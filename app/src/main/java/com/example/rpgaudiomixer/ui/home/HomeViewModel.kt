package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
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

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: Scene? = null,
    val topAtmosphere: MostPlayedSoundscapeTrack? = null,
    val legendaryAction: FxTrack? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                campaignRepository.observeActiveCampaign(),
                campaignRepository.observeActiveCampaign().flatMapLatest { campaign ->
                    campaign?.let { sessionRepository.observeLastOpenedSceneInCampaign(it.id) } ?: flowOf(null)
                },
                soundscapeRepository.observeMostPlayedTrack(),
                fxRepository.observeMostPlayedTrack(),
            ) { campaign, resumeScene, topAtmosphere, legendaryAction ->
                HomeUiState(
                    activeCampaign = campaign,
                    resumeScene = resumeScene,
                    topAtmosphere = topAtmosphere,
                    legendaryAction = legendaryAction,
                )
            }
                .catch { throwable ->
                    _uiState.value = HomeUiState(
                        activeCampaign = null,
                        resumeScene = null,
                        topAtmosphere = null,
                        legendaryAction = null,
                        errorMessage = throwable.message ?: "Unable to load home screen.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun openCampaign(campaignId: Long) {
        viewModelScope.launch {
            campaignRepository.markCampaignPlayed(campaignId)
        }
    }
}
