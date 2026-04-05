package com.example.rpgaudiomixer.app.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.FXRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: Scene? = null,
    val topAtmosphere: SoundscapeTrack? = null,
    val topAtmosphereCategory: String? = null,
    val legendaryAction: FXTrack? = null,
    val legendaryActionCategory: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FXRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeData()
    }

    private fun observeHomeData() {
        val latestCampaignFlow = campaignRepository.observeLatest()
        val topSoundscapeFlow = soundscapeRepository.observeMostPlayed()
        val topFxFlow = fxRepository.observeMostPlayed()
        val allCategoriesFlow = soundscapeRepository.observeAllCategories()

        combine(
            latestCampaignFlow,
            topSoundscapeFlow,
            topFxFlow,
            allCategoriesFlow
        ) { latestCampaign, topSoundscape, topFx, allCategories ->
            val atmosphereCategory = topSoundscape?.let { ts ->
                allCategories.find { it.id == ts.categoryId }?.name
            }
            // For FX, we'll use the first tag as category for now, as they don't have explicit categories in DB
            val fxCategory = topFx?.tags?.firstOrNull() ?: "Ambient"

            quadruple(latestCampaign, topSoundscape, atmosphereCategory, topFx, fxCategory)
        }.flatMapLatest { (latestCampaign, topSoundscape, atmosphereCategory, topFx, fxCategory) ->
            if (latestCampaign != null) {
                sessionRepository.observeLatestByCampaign(latestCampaign.id).flatMapLatest { latestSession ->
                    if (latestSession != null && latestSession.lastOpenedSceneId != null) {
                        sceneRepository.getById(latestSession.lastOpenedSceneId).map { resumeScene ->
                            HomeUiState(
                                activeCampaign = latestCampaign,
                                resumeScene = resumeScene,
                                topAtmosphere = topSoundscape,
                                topAtmosphereCategory = atmosphereCategory,
                                legendaryAction = topFx,
                                legendaryActionCategory = fxCategory,
                                isLoading = false
                            )
                        }
                    } else {
                        flowOf(
                            HomeUiState(
                                activeCampaign = latestCampaign,
                                resumeScene = null,
                                topAtmosphere = topSoundscape,
                                topAtmosphereCategory = atmosphereCategory,
                                legendaryAction = topFx,
                                legendaryActionCategory = fxCategory,
                                isLoading = false
                            )
                        )
                    }
                }
            } else {
                flowOf(
                    HomeUiState(
                        activeCampaign = null,
                        resumeScene = null,
                        topAtmosphere = topSoundscape,
                        topAtmosphereCategory = atmosphereCategory,
                        legendaryAction = topFx,
                        legendaryActionCategory = fxCategory,
                        isLoading = false
                    )
                )
            }
        }
        .onEach { newState ->
            _uiState.update { newState }
        }
        .launchIn(viewModelScope)
    }

    private fun <A, B, C, D, E> quadruple(a: A, b: B, c: C, d: D, e: E) = Quintuple(a, b, c, d, e)
}

data class Quintuple<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
