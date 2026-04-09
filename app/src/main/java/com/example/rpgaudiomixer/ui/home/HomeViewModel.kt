package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SoundscapeTrackDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    campaignDao: CampaignDao,
    soundscapeTrackDao: SoundscapeTrackDao,
    fxTrackDao: FxTrackDao,
    sceneDao: SceneDao
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        campaignDao.observeMostRecent(),
        soundscapeTrackDao.observeMostPlayed(),
        fxTrackDao.getMostPlayed()
    ) { mostRecentCampaign, mostPlayedSoundscape, mostPlayedFx ->
        HomeUiState(
            mostRecentCampaign = mostRecentCampaign?.let {
                HomeCampaignData(
                    id = it.id,
                    name = it.name,
                    coverArtUri = it.coverArtUri,
                    lastOpenedSceneId = it.lastOpenedSceneId
                )
            },
            mostPlayedSoundscape = mostPlayedSoundscape?.let {
                HomeTrackData(
                    id = it.id,
                    name = it.name,
                    playCount = it.playCount
                )
            },
            mostPlayedFx = mostPlayedFx?.let {
                HomeTrackData(
                    id = it.id,
                    name = it.name,
                    playCount = it.playCount
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}

data class HomeUiState(
    val mostRecentCampaign: HomeCampaignData? = null,
    val mostPlayedSoundscape: HomeTrackData? = null,
    val mostPlayedFx: HomeTrackData? = null
)

data class HomeCampaignData(
    val id: Long,
    val name: String,
    val coverArtUri: String?,
    val lastOpenedSceneId: Long?
)

data class HomeTrackData(
    val id: Long,
    val name: String,
    val playCount: Int
)
