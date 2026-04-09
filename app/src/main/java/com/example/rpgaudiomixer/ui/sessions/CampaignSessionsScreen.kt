package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@Composable
fun CampaignSessionsRoute(
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val campaignName by viewModel.campaignName.collectAsState(initial = "")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("Campaigns_Sessions_Placeholder"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Sessions list for $campaignName")
        Text("Session management arrives in Iteration 2.")
    }
}

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    campaignRepository: CampaignRepository,
) : ViewModel() {
    private val campaignId = requireNotNull(savedStateHandle.get<String>("campaignId")) {
        "Navigation argument 'campaignId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'campaignId' must be a valid numeric value.")

    val campaignName = campaignRepository.observeCampaign(campaignId)
        .map { campaign -> campaign?.name.orEmpty() }
}
