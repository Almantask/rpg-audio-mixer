package com.example.rpgaudiomixer.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        campaignRepository.observeMostRecent().flatMapLatest { campaign ->
            if (campaign?.lastOpenedSceneId != null) {
                sceneRepository.observeById(campaign.lastOpenedSceneId).map { scene ->
                    campaign to scene
                }
            } else {
                flowOf(campaign to null)
            }
        },
        soundscapeRepository.observeMostPlayedTrack(),
        fxRepository.observeMostPlayedTrack()
    ) { campaignData, topAtmosphere, legendaryAction ->
        val (campaign, scene) = campaignData
        HomeUiState(
            activeCampaign = campaign,
            resumeScene = scene,
            topAtmosphere = topAtmosphere,
            legendaryAction = legendaryAction,
            isLoading = false
        )
    }.catch { e ->
        emit(HomeUiState(isLoading = false, error = e.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )
}
