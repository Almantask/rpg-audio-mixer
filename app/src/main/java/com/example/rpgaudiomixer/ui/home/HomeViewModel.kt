package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.TrackType
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sceneRepository: SceneRepository,
    private val trackStatsRepository: TrackStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                val campaign = campaignRepository.getMostRecentlyPlayedCampaign()
                val scene = campaign?.let { sceneRepository.getLastOpenedSceneInCampaign(it.id) }
                val topAtmosphere = trackStatsRepository.getMostPlayedTrack(TrackType.LOOPABLE)
                val legendaryAction = trackStatsRepository.getMostPlayedTrack(TrackType.FX)

                _uiState.value = HomeUiState.Success(
                    activeCampaign = campaign,
                    lastScene = scene,
                    topAtmosphere = topAtmosphere,
                    legendaryAction = legendaryAction
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
