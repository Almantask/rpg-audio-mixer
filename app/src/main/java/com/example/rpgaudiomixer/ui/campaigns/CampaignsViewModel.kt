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

/**
 * UI State for Campaigns screen.
 */
sealed class CampaignsUiState {
    object Loading : CampaignsUiState()
    data class Success(val campaigns: List<Campaign>) : CampaignsUiState()
    data class Error(val message: String) : CampaignsUiState()
}

/**
 * ViewModel for Campaigns screen.
 *
 * Manages campaign CRUD operations and UI state.
 */
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

    /**
     * Create a new campaign.
     */
    fun createCampaign(name: String, coverArtUri: String? = null) {
        viewModelScope.launch {
            try {
                campaignRepository.create(name, coverArtUri)
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(
                    e.message ?: "Failed to create campaign"
                )
            }
        }
    }

    /**
     * Delete a campaign by ID.
     */
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

    /**
     * Clear error state.
     */
    fun clearError() {
        if (_uiState.value is CampaignsUiState.Error) {
            viewModelScope.launch {
                // Reload campaigns after clearing error
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
    }
}
