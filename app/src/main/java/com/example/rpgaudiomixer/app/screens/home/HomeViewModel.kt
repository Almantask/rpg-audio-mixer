package com.example.rpgaudiomixer.app.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val activeCampaign: Campaign?,
        val topAtmosphereTrack: String? = null,
        val legendaryAction: String? = null,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: CampaignRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.observeAll()
        .map<List<Campaign>, HomeUiState> { campaigns ->
            HomeUiState.Success(
                activeCampaign = campaigns.firstOrNull(),
                topAtmosphereTrack = null,
                legendaryAction = null,
            )
        }
        .catch { emit(HomeUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)
}
