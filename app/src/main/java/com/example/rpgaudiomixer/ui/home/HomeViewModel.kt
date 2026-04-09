package com.example.rpgaudiomixer.ui.home

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class HomeViewModel @Inject constructor(
    campaignRepository: CampaignRepository,
) : ViewModel() {
    val activeCampaign: Flow<Campaign?> = campaignRepository.observeMostRecentCampaign()
}
