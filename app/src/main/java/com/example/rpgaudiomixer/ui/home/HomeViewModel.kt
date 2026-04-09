package com.example.rpgaudiomixer.ui.home

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

data class HomeUiState(
    val activeCampaign: Campaign? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            campaignRepository.observeActiveCampaign()
                .catch { throwable ->
                    _uiState.value = HomeUiState(
                        activeCampaign = null,
                        errorMessage = throwable.message ?: "Unable to load home screen.",
                    )
                }
                .collect { campaign ->
                    _uiState.value = HomeUiState(activeCampaign = campaign)
                }
        }
    }

    fun openCampaign(campaignId: Long) {
        viewModelScope.launch {
            campaignRepository.markCampaignPlayed(campaignId)
        }
    }
}
