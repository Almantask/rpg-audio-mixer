package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.FxTrackEntity
import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.trash.DeletedItem
import com.example.rpgaudiomixer.domain.trash.DeletedItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Singleton
class TrashRepositoryImpl @Inject constructor(
    private val campaignDao: CampaignDao,
    private val sessionDao: SessionDao,
    private val sceneDao: SceneDao,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
    private val fxTrackDao: FxTrackDao,
) : TrashRepository {

    override fun observeDeletedItems(): Flow<List<DeletedItem>> = combine(
        campaignDao.observeDeleted(),
        sessionDao.observeDeleted(),
        sceneDao.observeDeleted(),
        soundscapeCategoryDao.observeDeleted(),
        fxTrackDao.observeDeleted(),
    ) { campaigns, sessions, scenes, categories, fxTracks ->
        buildList {
            addAll(campaigns.map(CampaignEntity::toDeletedItem))
            addAll(sessions.map(SessionEntity::toDeletedItem))
            addAll(scenes.map(SceneEntity::toDeletedItem))
            addAll(categories.map(SoundscapeCategoryEntity::toDeletedItem))
            addAll(fxTracks.map(FxTrackEntity::toDeletedItem))
        }.sortedByDescending(DeletedItem::deletedAt)
    }

    override suspend fun restoreItem(itemId: Long, type: DeletedItemType) {
        when (type) {
            DeletedItemType.CAMPAIGN -> campaignDao.restoreById(itemId)
            DeletedItemType.SESSION -> sessionDao.restoreById(itemId)
            DeletedItemType.SCENE -> sceneDao.restoreById(itemId)
            DeletedItemType.SOUNDSCAPE -> soundscapeCategoryDao.restoreById(itemId)
            DeletedItemType.FX -> fxTrackDao.restore(itemId)
        }
    }

    override suspend fun permanentlyDeleteItem(itemId: Long, type: DeletedItemType) {
        when (type) {
            DeletedItemType.CAMPAIGN -> campaignDao.deleteById(itemId)
            DeletedItemType.SESSION -> sessionDao.deleteById(itemId)
            DeletedItemType.SCENE -> sceneDao.deleteById(itemId)
            DeletedItemType.SOUNDSCAPE -> soundscapeCategoryDao.deleteById(itemId)
            DeletedItemType.FX -> fxTrackDao.deleteById(itemId)
        }
    }

    override suspend fun emptyVault() {
        campaignDao.deleteAllDeleted()
        sessionDao.deleteAllDeleted()
        sceneDao.deleteAllDeleted()
        soundscapeCategoryDao.deleteAllDeleted()
        fxTrackDao.deleteAllDeleted()
    }

    override suspend fun purgeItemsDeletedBefore(cutoffTimeMillis: Long) {
        campaignDao.purgeDeletedBefore(cutoffTimeMillis)
        sessionDao.purgeDeletedBefore(cutoffTimeMillis)
        sceneDao.purgeDeletedBefore(cutoffTimeMillis)
        soundscapeCategoryDao.purgeDeletedBefore(cutoffTimeMillis)
        fxTrackDao.purgeDeletedBefore(cutoffTimeMillis)
    }
}

private fun CampaignEntity.toDeletedItem(): DeletedItem = DeletedItem(
    id = id,
    name = name,
    type = DeletedItemType.CAMPAIGN,
    deletedAt = requireNotNull(deletedAt),
)

private fun SessionEntity.toDeletedItem(): DeletedItem = DeletedItem(
    id = id,
    name = name,
    type = DeletedItemType.SESSION,
    deletedAt = requireNotNull(deletedAt),
)

private fun SceneEntity.toDeletedItem(): DeletedItem = DeletedItem(
    id = id,
    name = name,
    type = DeletedItemType.SCENE,
    deletedAt = requireNotNull(deletedAt),
)

private fun SoundscapeCategoryEntity.toDeletedItem(): DeletedItem = DeletedItem(
    id = id,
    name = name,
    type = DeletedItemType.SOUNDSCAPE,
    deletedAt = requireNotNull(deletedAt),
)

private fun FxTrackEntity.toDeletedItem(): DeletedItem = DeletedItem(
    id = id,
    name = name,
    type = DeletedItemType.FX,
    deletedAt = requireNotNull(deletedAt),
)
