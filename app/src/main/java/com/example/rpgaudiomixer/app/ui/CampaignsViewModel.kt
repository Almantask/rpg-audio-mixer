package com.example.rpgaudiomixer.app.ui

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.storage.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    fun getCampaigns(): List<Campaign> = repository.getAllCampaigns()

    fun getSessionsForCampaign(campaignId: String) = repository.getSessionsForCampaign(campaignId)

    fun addSession(session: com.example.rpgaudiomixer.domain.model.Session) {
        repository.addSession(session)
    }

    fun addSampleCampaign() {
        val now = System.currentTimeMillis()
        repository.addCampaign(Campaign(name = "New Tale", description = "A newly scribed tale", lastPlayedAt = now))
    }

    fun resumeCampaign(campaignId: String) {
        repository.updateCampaign(repository.getCampaignById(campaignId)?.copy(lastPlayedAt = System.currentTimeMillis()) ?: return)
    }
}
