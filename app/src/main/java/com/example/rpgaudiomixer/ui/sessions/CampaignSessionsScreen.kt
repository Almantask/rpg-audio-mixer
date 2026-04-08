package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun CampaignSessionsRoute(
    modifier: Modifier = Modifier,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val campaignName by viewModel.campaignName.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Sessions for $campaignName",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Session management will be implemented in the next iteration.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    campaignRepository: CampaignRepository,
) : ViewModel() {

    private val campaignId: Long = checkNotNull(savedStateHandle[AppRoute.CAMPAIGN_ID_ARG])

    private val _campaignName = MutableStateFlow("Campaign")
    val campaignName = _campaignName.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.observeCampaign(campaignId).collect { campaign ->
                _campaignName.value = campaign?.name ?: "Campaign"
            }
        }
    }
}
