package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Campaign>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Campaign>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            try {
                campaignRepository.observeAll()
                    .catch { exception ->
                        _uiState.value = UiState.Error(exception.message ?: "Unknown error")
                    }
                    .collect { campaigns ->
                        _uiState.value = UiState.Success(campaigns)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load campaigns")
            }
        }
    }

    fun createCampaign(name: String, coverUri: String?) {
        if (name.isBlank()) {
            _errorMessage.value = "Campaign name cannot be empty"
            return
        }

        viewModelScope.launch {
            try {
                campaignRepository.create(name.trim(), coverUri)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create campaign: ${e.message}"
            }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            try {
                campaignRepository.delete(id)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete campaign: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
