package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {

    val campaigns: StateFlow<List<Campaign>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCampaign(name: String, coverUri: String?) {
        viewModelScope.launch {
            repository.upsert(Campaign(name = name, coverArtUri = coverUri))
        }
    }

    fun deleteCampaign(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }
}
