package com.example.rpgaudiomixer.app.ui

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val resumeScene: Scene? = null,
    val topAtmosphere: SoundscapeCategory? = null,
    val legendaryAction: SoundEffect? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GameRepository,
) : ViewModel() {

    val uiState: HomeUiState
        get() {
            val activeCampaign = repository.getActiveCampaign()
            val resumeScene = repository.getAllScenes().maxByOrNull { it.lastPlayedAt }
            return HomeUiState(
                activeCampaign = activeCampaign,
                resumeScene = resumeScene,
                topAtmosphere = repository.getAllSoundscapeCategories().maxByOrNull { it.playCount },
                legendaryAction = repository.getAllSoundEffects().maxByOrNull { it.playCount }
            )
        }

    fun markCampaignPlayed(campaignId: String) {
        val campaign = repository.getCampaignById(campaignId) ?: return
        repository.updateCampaign(campaign.copy(lastPlayedAt = System.currentTimeMillis()))
    }

    fun markScenePlayed(sceneId: String) {
        repository.getAllScenes().find { it.id == sceneId }?.let { scene ->
            repository.updateScene(scene.copy(lastPlayedAt = System.currentTimeMillis()))
        }
    }
}
