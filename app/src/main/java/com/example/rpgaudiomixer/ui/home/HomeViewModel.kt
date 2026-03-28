package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.storage.CampaignRepository
import com.example.rpgaudiomixer.domain.storage.FxRepository
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val lastScene: Scene? = null,
    val topAtmosphereScene: Scene? = null,
    val legendaryFx: FxEffect? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    campaignRepository: CampaignRepository,
    sceneRepository: SceneRepository,
    fxRepository: FxRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        campaignRepository.getAllCampaigns(),
        sceneRepository.getAllScenes(),
        fxRepository.getAllEffects(),
    ) { campaigns, scenes, fx ->
        HomeUiState(
            activeCampaign = campaigns.maxByOrNull { it.lastPlayedAt },
            lastScene = scenes.maxByOrNull { it.createdAt },
            topAtmosphereScene = scenes.maxByOrNull { it.atmosphereMasterVolume * it.playCount },
            legendaryFx = fx.maxByOrNull { it.playCount },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )
}
