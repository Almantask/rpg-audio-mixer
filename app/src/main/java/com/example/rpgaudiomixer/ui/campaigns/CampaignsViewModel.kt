package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignsUiState>(CampaignsUiState.Loading)
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            try {
                val campaigns = campaignRepository.getAllCampaigns()
                _uiState.value = CampaignsUiState.Success(campaigns)
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createCampaign(name: String) {
        viewModelScope.launch {
            try {
                campaignRepository.createCampaign(name)
                loadCampaigns()
                hideCreateDialog()
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(e.message ?: "Failed to create campaign")
            }
        }
    }

    fun deleteCampaign(id: String) {
        viewModelScope.launch {
            try {
                campaignRepository.deleteCampaign(id)
                loadCampaigns()
            } catch (e: Exception) {
                _uiState.value = CampaignsUiState.Error(e.message ?: "Failed to delete campaign")
            }
        }
    }

    fun showCreateDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateDialog() {
        _showCreateDialog.value = false
    }
}
