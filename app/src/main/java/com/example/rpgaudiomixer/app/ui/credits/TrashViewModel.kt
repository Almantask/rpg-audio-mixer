package com.example.rpgaudiomixer.app.ui.credits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository
) : ViewModel() {

    val uiState: StateFlow<TrashUiState> = combine(
        campaignRepository.observeDeleted(),
        sessionRepository.observeDeleted(),
        sceneRepository.observeDeleted(),
        soundscapeRepository.observeDeletedCategories(),
        fxRepository.observeDeleted()
    ) { campaigns, sessions, scenes, categories, fxs ->
        val allItems = mutableListOf<TrashItem>()
        allItems.addAll(campaigns.map { TrashItem.CampaignItem(it) })
        allItems.addAll(sessions.map { TrashItem.SessionItem(it) })
        allItems.addAll(scenes.map { TrashItem.SceneItem(it) })
        allItems.addAll(categories.map { TrashItem.CategoryItem(it) })
        allItems.addAll(fxs.map { TrashItem.FxItem(it) })
        
        TrashUiState(
            items = allItems.sortedByDescending { it.deletedAt },
            isLoading = false
        )
    }.catch { e ->
        emit(TrashUiState(isLoading = false, error = e.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrashUiState(isLoading = true)
    )

    fun restore(item: TrashItem) {
        viewModelScope.launch {
            when (item) {
                is TrashItem.CampaignItem -> campaignRepository.restore(item.id)
                is TrashItem.SessionItem -> sessionRepository.restore(item.id)
                is TrashItem.SceneItem -> sceneRepository.restore(item.id)
                is TrashItem.CategoryItem -> soundscapeRepository.restoreCategory(item.id)
                is TrashItem.FxItem -> fxRepository.restore(item.id)
            }
        }
    }

    fun permanentlyDelete(item: TrashItem) {
        viewModelScope.launch {
            when (item) {
                is TrashItem.CampaignItem -> campaignRepository.delete(item.id)
                is TrashItem.SessionItem -> sessionRepository.delete(item.id)
                is TrashItem.SceneItem -> sceneRepository.delete(item.id)
                is TrashItem.CategoryItem -> soundscapeRepository.deleteCategory(item.id)
                is TrashItem.FxItem -> fxRepository.delete(item.id)
            }
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            uiState.value.items.forEach { permanentlyDelete(it) }
        }
    }
}
