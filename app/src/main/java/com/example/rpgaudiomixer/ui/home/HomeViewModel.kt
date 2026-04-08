package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.FxRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: Scene? = null,
    val topAtmosphere: SoundscapeTrack? = null,
    val legendaryAction: FxTrack? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                combine(
                    campaignRepository.observeAll(), // Get most recent campaign
                    sceneRepository.observeAll(), // Get most recent scene
                    soundscapeRepository.observeAllCategories(), // For top atmosphere
                    fxRepository.observeAll() // For legendary action
                ) { campaigns, scenes, categories, fxTracks ->
                    val activeCampaign = campaigns.maxByOrNull { it.lastPlayedAt }
                    val resumeScene = scenes.maxByOrNull { it.id } // Simplified - would need proper tracking

                    // Get most played soundscape track
                    var topAtmosphere: SoundscapeTrack? = null
                    for (category in categories) {
                        val tracks = soundscapeRepository.observeTracksByCategory(category.id).first()
                        val mostPlayed = tracks.maxByOrNull { it.playCount }
                        if (mostPlayed != null && (topAtmosphere == null || mostPlayed.playCount > topAtmosphere.playCount)) {
                            topAtmosphere = mostPlayed
                        }
                    }

                    val legendaryAction = fxTracks.maxByOrNull { it.playCount }

                    HomeUiState(
                        activeCampaign = activeCampaign,
                        resumeScene = resumeScene,
                        topAtmosphere = topAtmosphere,
                        legendaryAction = legendaryAction
                    )
                }.collect { homeState ->
                    _uiState.value = UiState.Success(homeState)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load home data")
            }
        }
    }
}
