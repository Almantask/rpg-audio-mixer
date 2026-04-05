package com.example.rpgaudiomixer.app.screens.credits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DeletedItem(
    open val id: Long,
    open val name: String,
    open val deletedAt: Long,
    val type: String
) {
    data class CampaignItem(override val id: Long, override val name: String, override val deletedAt: Long) : DeletedItem(id, name, deletedAt, "CAMPAIGN")
    data class SessionItem(override val id: Long, override val name: String, override val deletedAt: Long) : DeletedItem(id, name, deletedAt, "SESSION")
    data class SceneItem(override val id: Long, override val name: String, override val deletedAt: Long) : DeletedItem(id, name, deletedAt, "SCENE")
    data class CategoryItem(override val id: Long, override val name: String, override val deletedAt: Long) : DeletedItem(id, name, deletedAt, "CATEGORY")
    data class FXItem(override val id: Long, override val name: String, override val deletedAt: Long) : DeletedItem(id, name, deletedAt, "FX")
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FXRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<DeletedItem>>(emptyList())
    val items: StateFlow<List<DeletedItem>> = combine(
        campaignRepository.observeDeleted().map { list -> list.map { DeletedItem.CampaignItem(it.id, it.name, it.deletedAt ?: 0L) } },
        sessionRepository.observeDeleted().map { list -> list.map { DeletedItem.SessionItem(it.id, it.name, it.deletedAt ?: 0L) } },
        sceneRepository.observeDeleted().map { list -> list.map { DeletedItem.SceneItem(it.id, it.name, it.deletedAt ?: 0L) } },
        soundscapeRepository.observeDeletedCategories().map { list -> list.map { DeletedItem.CategoryItem(it.id, it.name, it.deletedAt ?: 0L) } },
        fxRepository.observeDeleted().map { list -> list.map { DeletedItem.FXItem(it.id, it.name, it.deletedAt ?: 0L) } }
    ) { arrays ->
        arrays.flatMap { it }.sortedByDescending { it.deletedAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restore(item: DeletedItem) {
        viewModelScope.launch {
            when (item) {
                is DeletedItem.CampaignItem -> campaignRepository.restore(item.id)
                is DeletedItem.SessionItem -> sessionRepository.restore(item.id)
                is DeletedItem.SceneItem -> sceneRepository.restore(item.id)
                is DeletedItem.CategoryItem -> soundscapeRepository.restoreCategory(item.id)
                is DeletedItem.FXItem -> fxRepository.restore(item.id)
            }
        }
    }

    fun permanentDelete(item: DeletedItem) {
        viewModelScope.launch {
            when (item) {
                is DeletedItem.CampaignItem -> campaignRepository.permanentDelete(item.id)
                is DeletedItem.SessionItem -> sessionRepository.permanentDelete(item.id)
                is DeletedItem.SceneItem -> sceneRepository.permanentDelete(item.id)
                is DeletedItem.CategoryItem -> soundscapeRepository.permanentDeleteCategory(item.id)
                is DeletedItem.FXItem -> fxRepository.permanentDelete(item.id)
            }
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            // Purge everything immediately if you empty vault
            campaignRepository.purgeOldDeleted(System.currentTimeMillis() + 1000)
            sessionRepository.purgeOldDeleted(System.currentTimeMillis() + 1000)
            sceneRepository.purgeOldDeleted(System.currentTimeMillis() + 1000)
            soundscapeRepository.purgeOldDeletedCategories(System.currentTimeMillis() + 1000)
            fxRepository.purgeOldDeleted(System.currentTimeMillis() + 1000)
        }
    }
}
