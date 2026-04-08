package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Campaigns screen
 * Manages campaign list state and CRUD operations
 */
@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Campaign>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Campaign>>> = _uiState.asStateFlow()

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
                .map<List<Campaign>, UiState<List<Campaign>>> { campaigns ->
                    UiState.Success(campaigns)
                }
                .catch { error ->
                    emit(UiState.Error(error.message ?: "Unknown error"))
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createCampaign(name: String, coverUri: String?) {
        viewModelScope.launch {
            try {
                val campaign = Campaign(
                    name = name,
                    coverArtUri = coverUri,
                    lastPlayedAt = System.currentTimeMillis()
                )
                campaignRepository.upsert(campaign)
                hideCreateDialog()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create campaign: ${e.message}"
            }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            try {
                campaignRepository.deleteById(id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete campaign: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
