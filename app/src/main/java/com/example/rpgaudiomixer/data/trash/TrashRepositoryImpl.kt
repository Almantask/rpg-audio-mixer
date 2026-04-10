package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TrashRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
    private val sessionDao: SessionDao,
    private val sceneDao: SceneDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
    private val fxTrackDao: FxTrackDao,
) : TrashRepository {

    override fun observeDeletedItems(): Flow<List<TrashItem>> {
        return combine(
            campaignDao.observeDeleted(),
            sessionDao.observeDeleted(),
            sceneDao.observeDeleted(),
            soundscapeCategoryDao.observeDeleted(),
            fxTrackDao.observeDeleted(),
        ) { campaigns, sessions, scenes, categories, fxTracks ->
            buildList {
                addAll(campaigns.map { it.toTrashItem() })
                addAll(sessions.map { it.toTrashItem() })
                addAll(scenes.map { it.toTrashItem() })
                addAll(categories.map { it.toTrashItem() })
                addAll(fxTracks.map { it.toTrashItem() })
            }.sortedWith(compareByDescending<TrashItem> { it.deletedAt }.thenBy { it.title.lowercase() })
        }
    }

    override suspend fun restoreItem(itemId: Long, itemType: TrashItemType) {
        when (itemType) {
            TrashItemType.CAMPAIGN -> campaignDao.restore(itemId)
            TrashItemType.SESSION -> sessionDao.restore(itemId)
            TrashItemType.SCENE -> sceneDao.restore(itemId)
            TrashItemType.SOUNDSCAPE -> soundscapeCategoryDao.restore(itemId)
            TrashItemType.FX -> fxTrackDao.restore(itemId)
        }
    }

    override suspend fun permanentlyDeleteItem(itemId: Long, itemType: TrashItemType) {
        when (itemType) {
            TrashItemType.CAMPAIGN -> campaignDao.hardDeleteById(itemId)
            TrashItemType.SESSION -> sessionDao.hardDeleteById(itemId)
            TrashItemType.SCENE -> sceneDao.hardDeleteById(itemId)
            TrashItemType.SOUNDSCAPE -> soundscapeCategoryDao.hardDeleteById(itemId)
            TrashItemType.FX -> fxTrackDao.hardDeleteById(itemId)
        }
    }

    override suspend fun emptyVault() {
        fxTrackDao.deleteAllDeleted()
        soundscapeCategoryDao.deleteAllDeleted()
        sceneDao.deleteAllDeleted()
        sessionDao.deleteAllDeleted()
        campaignDao.deleteAllDeleted()
    }

    override suspend fun purgeExpiredItems(currentTimeMillis: Long) {
        val cutoffMillis = currentTimeMillis - RETENTION_MILLIS
        fxTrackDao.purgeDeletedBefore(cutoffMillis)
        soundscapeCategoryDao.purgeDeletedBefore(cutoffMillis)
        sceneDao.purgeDeletedBefore(cutoffMillis)
        sessionDao.purgeDeletedBefore(cutoffMillis)
        campaignDao.purgeDeletedBefore(cutoffMillis)
    }

    companion object {
        private const val RETENTION_MILLIS = 7 * 24 * 60 * 60 * 1000L
    }
}

private fun CampaignEntity.toTrashItem(): TrashItem {
    return TrashItem(
        id = id,
        title = name,
        type = TrashItemType.CAMPAIGN,
        deletedAt = checkNotNull(deletedAt),
    )
}

private fun SessionEntity.toTrashItem(): TrashItem {
    return TrashItem(
        id = id,
        title = name,
        type = TrashItemType.SESSION,
        deletedAt = checkNotNull(deletedAt),
    )
}

private fun SceneEntity.toTrashItem(): TrashItem {
    return TrashItem(
        id = id,
        title = name,
        type = TrashItemType.SCENE,
        deletedAt = checkNotNull(deletedAt),
    )
}

private fun SoundscapeCategoryEntity.toTrashItem(): TrashItem {
    return TrashItem(
        id = id,
        title = name,
        type = TrashItemType.SOUNDSCAPE,
        deletedAt = checkNotNull(deletedAt),
    )
}

private fun FxTrackEntity.toTrashItem(): TrashItem {
    return TrashItem(
        id = id,
        title = name,
        type = TrashItemType.FX,
        deletedAt = checkNotNull(deletedAt),
    )
}
