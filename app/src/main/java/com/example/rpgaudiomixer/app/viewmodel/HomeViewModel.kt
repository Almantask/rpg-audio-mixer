package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Track
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val lastScene: Scene? = null,
    val topAtmosphereTrack: Track? = null,
    val legendaryFXTrack: FXTrack? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sceneRepository: SceneRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.getMostRecentCampaign().collect { campaign ->
                _uiState.value = _uiState.value.copy(activeCampaign = campaign)
            }
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                topAtmosphereTrack = libraryRepository.getMostPlayedLoopingTrack(),
                legendaryFXTrack = libraryRepository.getMostPlayedFXTrack(),
            )
        }
        // Last scene = first in the global scene list for now (no per-session tracking)
        viewModelScope.launch {
            sceneRepository.getAllScenes().collect { scenes ->
                _uiState.value = _uiState.value.copy(lastScene = scenes.firstOrNull())
            }
        }
    }
}
