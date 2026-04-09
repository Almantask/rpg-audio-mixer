package com.example.rpgaudiomixer.ui.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CampaignsUiState(
    val isLoading: Boolean = true,
    val campaigns: List<Campaign> = emptyList(),
    val showCreateDialog: Boolean = false,
    val draftName: String = "",
    val draftCoverArtUri: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val coverArtSelectionRepository: CampaignCoverArtSelectionRepository,
    private val campaignTrashRepository: CampaignTrashRepository,
    photoPickerMode: CampaignPhotoPickerMode,
) : ViewModel() {

    private val draftState = MutableStateFlow(CampaignDraft())
    private val _uiState = MutableStateFlow(CampaignsUiState())
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()
    val useSystemPhotoPicker: Boolean = photoPickerMode.useSystemPhotoPicker

    init {
        viewModelScope.launch {
            combine(
                campaignRepository.observeCampaigns(),
                draftState,
                coverArtSelectionRepository.selectedCoverArtUri,
            ) { campaigns, draft, selectedCoverArtUri ->
                val coverArtUri = resolveCoverArtUri(
                    draft = draft,
                    selectedCoverArtUri = selectedCoverArtUri,
                )
                CampaignsUiState(
                    isLoading = false,
                    campaigns = campaigns,
                    showCreateDialog = draft.isOpen,
                    draftName = draft.name,
                    draftCoverArtUri = coverArtUri,
                    errorMessage = draft.errorMessage,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openCreateDialog() {
        coverArtSelectionRepository.reset()
        draftState.value = CampaignDraft(isOpen = true)
    }

    fun dismissCreateDialog() {
        coverArtSelectionRepository.reset()
        draftState.value = CampaignDraft()
    }

    fun updateDraftName(name: String) {
        draftState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onCoverArtPicked(uri: String?) {
        coverArtSelectionRepository.updateSelectedCoverArt(uri)
        draftState.update { it.copy(coverArtUri = uri) }
    }

    fun confirmCreateCampaign() {
        val currentDraft = draftState.value
        if (currentDraft.name.isBlank()) {
            draftState.update { it.copy(errorMessage = "Every campaign needs a name.") }
            return
        }

        viewModelScope.launch {
            campaignRepository.upsertCampaign(
                Campaign(
                    name = currentDraft.name.trim(),
                    coverArtUri = resolveCoverArtUri(
                        draft = currentDraft,
                        selectedCoverArtUri = coverArtSelectionRepository.selectedCoverArtUri.value,
                    ),
                    lastPlayedAt = 0L,
                ),
            )
            dismissCreateDialog()
        }
    }

    fun markCampaignPlayed(campaignId: Long) {
        viewModelScope.launch {
            campaignRepository.updateLastPlayedAt(
                id = campaignId,
                lastPlayedAt = System.currentTimeMillis(),
            )
        }
    }

    fun deleteCampaign(campaign: Campaign) {
        viewModelScope.launch {
            campaignRepository.deleteCampaign(campaign.id)
            campaignTrashRepository.recordDeletedCampaign(campaign.name)
        }
    }

    fun clearError() {
        draftState.update { it.copy(errorMessage = null) }
    }

    private fun resolveCoverArtUri(
        draft: CampaignDraft,
        selectedCoverArtUri: String?,
    ): String? = selectedCoverArtUri ?: draft.coverArtUri
}

private data class CampaignDraft(
    val isOpen: Boolean = false,
    val name: String = "",
    val coverArtUri: String? = null,
    val errorMessage: String? = null,
)
