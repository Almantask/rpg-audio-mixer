package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CampaignsUiState {
    data object Loading : CampaignsUiState
    data class Success(val campaigns: List<Campaign>) : CampaignsUiState
    data class Error(val message: String) : CampaignsUiState
}

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignsUiState>(CampaignsUiState.Loading)
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            try {
                campaignRepository.observeAll()
                    .catch { e ->
                        _uiState.value = CampaignsUiState.Error(
                            e.message ?: "Failed to load campaigns"
                        )
                    }
                    .collect { campaigns ->
                        _uiState.value = CampaignsUiState.Success(campaigns)
                    }
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(
                    e.message ?: "Failed to load campaigns"
                )
            }
        }
    }

    fun createCampaign(name: String, coverUri: String?) {
        viewModelScope.launch {
            try {
                campaignRepository.create(name, coverUri)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create campaign"
            }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            try {
                campaignRepository.delete(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete campaign"
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}
