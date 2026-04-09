package com.example.rpgaudiomixer.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.domain.model.TrashItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val campaignDao: CampaignDao,
    private val sessionDao: SessionDao,
    private val sceneDao: SceneDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
    private val fxTrackDao: FxTrackDao
) : ViewModel() {

    val deletedItems: StateFlow<List<TrashItem>> = combine(
        campaignDao.observeDeleted(),
        sessionDao.observeDeleted(),
        sceneDao.observeDeleted(),
        soundscapeCategoryDao.observeDeleted(),
        fxTrackDao.observeDeleted()
    ) { campaigns, sessions, scenes, categories, fxTracks ->
        val items = mutableListOf<TrashItem>()

        items.addAll(campaigns.map { entity ->
            TrashItem.Campaign(
                id = entity.id,
                name = entity.name,
                deletedAt = entity.deletedAt ?: 0L,
                coverArtUri = entity.coverArtUri
            )
        })

        items.addAll(sessions.map { entity ->
            TrashItem.Session(
                id = entity.id,
                name = entity.name,
                deletedAt = entity.deletedAt ?: 0L,
                campaignId = entity.campaignId
            )
        })

        items.addAll(scenes.map { entity ->
            TrashItem.Scene(
                id = entity.id,
                name = entity.name,
                deletedAt = entity.deletedAt ?: 0L,
                description = entity.description
            )
        })

        items.addAll(categories.map { entity ->
            TrashItem.SoundscapeCategory(
                id = entity.id,
                name = entity.name,
                deletedAt = entity.deletedAt ?: 0L
            )
        })

        items.addAll(fxTracks.map { entity ->
            TrashItem.FxTrack(
                id = entity.id,
                name = entity.name,
                deletedAt = entity.deletedAt ?: 0L
            )
        })

        items.sortedByDescending { it.deletedAt }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun restoreItem(item: TrashItem) {
        viewModelScope.launch {
            when (item) {
                is TrashItem.Campaign -> campaignDao.restore(item.id)
                is TrashItem.Session -> sessionDao.restore(item.id)
                is TrashItem.Scene -> sceneDao.restore(item.id)
                is TrashItem.SoundscapeCategory -> soundscapeCategoryDao.restore(item.id)
                is TrashItem.FxTrack -> fxTrackDao.restore(item.id)
            }
        }
    }

    fun permanentlyDeleteItem(item: TrashItem) {
        viewModelScope.launch {
            when (item) {
                is TrashItem.Campaign -> {
                    val entity = campaignDao.getById(item.id)
                    entity?.let { campaignDao.delete(it) }
                }
                is TrashItem.Session -> {
                    val entity = sessionDao.getById(item.id)
                    entity?.let { sessionDao.delete(it) }
                }
                is TrashItem.Scene -> {
                    val entity = sceneDao.getById(item.id)
                    entity?.let { sceneDao.delete(it) }
                }
                is TrashItem.SoundscapeCategory -> {
                    val entity = soundscapeCategoryDao.getById(item.id)
                    entity?.let { soundscapeCategoryDao.delete(it) }
                }
                is TrashItem.FxTrack -> {
                    fxTrackDao.delete(item.id)
                }
            }
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            deletedItems.value.forEach { item ->
                permanentlyDeleteItem(item)
            }
        }
    }
}
