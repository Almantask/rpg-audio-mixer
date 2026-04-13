package com.example.rpgaudiomixer.app.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.model.TrashItem
import com.example.rpgaudiomixer.app.domain.model.TrashItemType
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TrashUiState {
    data object Loading : TrashUiState
    data class Success(val items: List<TrashItem>) : TrashUiState
    data class Error(val message: String) : TrashUiState
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository
) : ViewModel() {

    val uiState: StateFlow<TrashUiState> = combine(
        campaignRepository.observeDeleted(),
        sessionRepository.observeDeleted(),
        sceneRepository.observeDeleted()
    ) { campaigns, sessions, scenes ->
        val items = buildList {
            campaigns.forEach { c -> add(TrashItem(c.id, c.name, TrashItemType.CAMPAIGN, c.deletedAt ?: 0L)) }
            sessions.forEach { s -> add(TrashItem(s.id, s.name, TrashItemType.SESSION, s.deletedAt ?: 0L)) }
            scenes.forEach { sc -> add(TrashItem(sc.id, sc.name, TrashItemType.SCENE, sc.deletedAt ?: 0L)) }
        }.sortedByDescending { it.deletedAt }
        TrashUiState.Success(items) as TrashUiState
    }
        .catch { emit(TrashUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrashUiState.Loading)

    fun restore(item: TrashItem) {
        viewModelScope.launch {
            when (item.type) {
                TrashItemType.CAMPAIGN -> campaignRepository.restore(Campaign(item.id, item.name))
                TrashItemType.SESSION -> sessionRepository.restore(Session(item.id, 0L, item.name))
                TrashItemType.SCENE -> sceneRepository.restore(Scene(item.id, item.name))
            }
        }
    }

    fun hardDelete(item: TrashItem) {
        viewModelScope.launch {
            when (item.type) {
                TrashItemType.CAMPAIGN -> campaignRepository.hardDelete(Campaign(item.id, item.name))
                TrashItemType.SESSION -> sessionRepository.hardDelete(Session(item.id, 0L, item.name))
                TrashItemType.SCENE -> sceneRepository.hardDelete(Scene(item.id, item.name))
            }
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            campaignRepository.purgeAllDeleted()
            sessionRepository.purgeAllDeleted()
            sceneRepository.purgeAllDeleted()
        }
    }
}
