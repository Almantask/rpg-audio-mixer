package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
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
                addAll(
                    campaigns.mapNotNull { entity ->
                        entity.deletedAt?.let { deletedAt ->
                            TrashItem(
                                id = entity.id,
                                title = entity.name,
                                subtitle = entity.coverArtUri,
                                deletedAt = deletedAt,
                                type = TrashItemType.CAMPAIGN,
                            )
                        }
                    },
                )
                addAll(
                    sessions.mapNotNull { entity ->
                        entity.deletedAt?.let { deletedAt ->
                            TrashItem(
                                id = entity.id,
                                title = entity.name,
                                subtitle = entity.coverArtUri,
                                deletedAt = deletedAt,
                                type = TrashItemType.SESSION,
                            )
                        }
                    },
                )
                addAll(
                    scenes.mapNotNull { entity ->
                        entity.deletedAt?.let { deletedAt ->
                            TrashItem(
                                id = entity.id,
                                title = entity.name,
                                subtitle = entity.description,
                                deletedAt = deletedAt,
                                type = TrashItemType.SCENE,
                            )
                        }
                    },
                )
                addAll(
                    categories.mapNotNull { entity ->
                        entity.deletedAt?.let { deletedAt ->
                            TrashItem(
                                id = entity.id,
                                title = entity.name,
                                subtitle = entity.themeLabel,
                                deletedAt = deletedAt,
                                type = TrashItemType.SOUNDSCAPE,
                            )
                        }
                    },
                )
                addAll(
                    fxTracks.mapNotNull { entity ->
                        entity.deletedAt?.let { deletedAt ->
                            TrashItem(
                                id = entity.id,
                                title = entity.name,
                                subtitle = entity.tags,
                                deletedAt = deletedAt,
                                type = TrashItemType.FX,
                            )
                        }
                    },
                )
            }.sortedByDescending { item -> item.deletedAt }
        }
    }

    override suspend fun restore(type: TrashItemType, id: Long) {
        when (type) {
            TrashItemType.CAMPAIGN -> campaignDao.restore(id)
            TrashItemType.SESSION -> sessionDao.restore(id)
            TrashItemType.SCENE -> sceneDao.restore(id)
            TrashItemType.SOUNDSCAPE -> soundscapeCategoryDao.restore(id)
            TrashItemType.FX -> fxTrackDao.restore(id)
        }
    }

    override suspend fun permanentlyDelete(type: TrashItemType, id: Long) {
        when (type) {
            TrashItemType.CAMPAIGN -> campaignDao.delete(id)
            TrashItemType.SESSION -> sessionDao.delete(id)
            TrashItemType.SCENE -> sceneDao.delete(id)
            TrashItemType.SOUNDSCAPE -> soundscapeCategoryDao.delete(id)
            TrashItemType.FX -> fxTrackDao.delete(id)
        }
    }

    override suspend fun emptyVault() {
        fxTrackDao.deleteAllDeleted()
        soundscapeCategoryDao.deleteAllDeleted()
        sceneDao.deleteAllDeleted()
        sessionDao.deleteAllDeleted()
        campaignDao.deleteAllDeleted()
    }

    override suspend fun purgeExpired() {
        val cutoffMillis = System.currentTimeMillis() - TRASH_RETENTION_MS
        fxTrackDao.purgeDeletedBefore(cutoffMillis)
        soundscapeCategoryDao.purgeDeletedBefore(cutoffMillis)
        sceneDao.purgeDeletedBefore(cutoffMillis)
        sessionDao.purgeDeletedBefore(cutoffMillis)
        campaignDao.purgeDeletedBefore(cutoffMillis)
    }

    private companion object {
        private const val TRASH_RETENTION_MS = 7 * 24 * 60 * 60 * 1000L
    }
}
