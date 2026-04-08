package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            campaignRepository.observeAll()
                .catch { e ->
                    _uiState.value = CampaignsUiState.Error(
                        e.message ?: "Failed to load campaigns"
                    )
                }
                .collect { campaigns ->
                    _uiState.value = CampaignsUiState.Success(campaigns)
                }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createCampaign(name: String, coverArtUri: String? = null) {
        viewModelScope.launch {
            try {
                campaignRepository.create(name, coverArtUri)
                hideCreateDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to create campaign"
            }
        }
    }

    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            try {
                campaignRepository.delete(campaign)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete campaign"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
