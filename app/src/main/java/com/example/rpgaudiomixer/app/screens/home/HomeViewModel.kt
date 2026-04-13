package com.example.rpgaudiomixer.app.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val latestCampaign: Campaign?,
        val latestScene: Scene?
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sceneRepository: SceneRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        campaignRepository.observeLatest(),
        sceneRepository.observeLatest()
    ) { campaign, scene -> HomeUiState.Success(campaign, scene) as HomeUiState }
        .catch { emit(HomeUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading)
}
