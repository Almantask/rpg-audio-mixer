package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Campaign>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Campaign>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeCampaigns()
    }

    fun createCampaign(name: String, coverUri: String?) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _errorMessage.value = "Campaign name is required."
            return
        }

        viewModelScope.launch {
            runCatching {
                campaignRepository.createCampaign(
                    name = trimmedName,
                    coverArtUri = coverUri,
                )
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to create campaign."
            }
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            runCatching {
                campaignRepository.deleteCampaign(id)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to delete campaign."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun observeCampaigns() {
        viewModelScope.launch {
            campaignRepository.observeAll()
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load campaigns.",
                    )
                }
                .collect { campaigns ->
                    _uiState.value = UiState.Success(campaigns)
                }
        }
    }
}
