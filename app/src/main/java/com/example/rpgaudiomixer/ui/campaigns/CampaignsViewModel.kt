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
    private val repository: CampaignRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignsUiState>(CampaignsUiState.Loading)
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll()
                .catch { e -> _uiState.value = CampaignsUiState.Error(e.message ?: "Unknown error") }
                .collect { campaigns -> _uiState.value = CampaignsUiState.Success(campaigns) }
        }
    }

    fun createCampaign(name: String, coverArtUri: String?) {
        viewModelScope.launch {
            runCatching { repository.create(name, coverArtUri) }
                .onFailure { e -> _uiState.value = CampaignsUiState.Error(e.message ?: "Unknown error") }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            runCatching { repository.delete(id) }
                .onFailure { e -> _uiState.value = CampaignsUiState.Error(e.message ?: "Unknown error") }
        }
    }
}
