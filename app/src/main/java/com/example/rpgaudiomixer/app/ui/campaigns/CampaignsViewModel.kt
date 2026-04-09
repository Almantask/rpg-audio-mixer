package com.example.rpgaudiomixer.app.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {

    val uiState: StateFlow<CampaignsUiState> = repository.observeAll()
        .map { CampaignsUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CampaignsUiState.Loading
        )

    fun createCampaign(name: String, coverUri: String?) {
        viewModelScope.launch {
            repository.upsert(Campaign(name = name, coverArtUri = coverUri))
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
