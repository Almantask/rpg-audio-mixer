package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
) : ViewModel() {
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        campaignRepository: CampaignRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(campaignRepository) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow<UiState<List<Campaign>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            campaignRepository.observeCampaigns()
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        message = throwable.message ?: "Unable to load campaigns.",
                    )
                }
                .collect { campaigns ->
                    _uiState.update { UiState.Success(campaigns) }
                }
        }
    }

    fun createCampaign(name: String, coverArtUri: String?) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }

        viewModelScope.launch(mainDispatcher) {
            campaignRepository.createCampaign(
                name = trimmedName,
                coverArtUri = coverArtUri,
            )
        }
    }

    fun deleteCampaign(campaignId: Long) {
        viewModelScope.launch(mainDispatcher) {
            campaignRepository.deleteCampaign(campaignId)
        }
    }
}
