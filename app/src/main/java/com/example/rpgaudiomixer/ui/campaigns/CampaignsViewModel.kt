package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignsUiState>(CampaignsUiState.Loading)
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            campaignRepository.observeAll()
                .catch { error ->
                    _uiState.value = CampaignsUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
                .collect { campaigns ->
                    _uiState.value = CampaignsUiState.Success(campaigns)
                }
        }
    }

    fun createCampaign(name: String, coverUri: String?) {
        viewModelScope.launch {
            try {
                campaignRepository.create(name, coverUri)
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(
                    e.message ?: "Failed to create campaign"
                )
            }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            try {
                campaignRepository.delete(id)
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(
                    e.message ?: "Failed to delete campaign"
                )
            }
        }
    }
}
