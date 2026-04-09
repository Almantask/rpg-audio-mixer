package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface CampaignsUiState {
    data object Loading : CampaignsUiState
    data class Success(val campaigns: List<Campaign>) : CampaignsUiState
    data class Error(val message: String) : CampaignsUiState
}

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CampaignsUiState>(CampaignsUiState.Loading)
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.observeCampaigns()
                .catch { throwable ->
                    _uiState.value = CampaignsUiState.Error(
                        throwable.message ?: "Unable to load campaigns.",
                    )
                }
                .collect { campaigns ->
                    _uiState.value = CampaignsUiState.Success(campaigns)
                }
        }
    }

    fun createCampaign(name: String, coverArtUri: String?) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }

        viewModelScope.launch {
            campaignRepository.createCampaign(trimmedName, coverArtUri)
        }
    }

    fun deleteCampaign(campaignId: Long) {
        viewModelScope.launch {
            campaignRepository.deleteCampaign(campaignId)
        }
    }

    fun openCampaign(campaignId: Long) {
        viewModelScope.launch {
            campaignRepository.markCampaignPlayed(campaignId)
        }
    }
}
