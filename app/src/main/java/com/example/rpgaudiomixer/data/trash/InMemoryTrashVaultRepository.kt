package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.trash.TrashItem
import com.example.rpgaudiomixer.domain.trash.TrashItemType
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

private const val TRASH_RETENTION_DAYS = 7L
private val TRASH_RETENTION_MILLIS = TimeUnit.DAYS.toMillis(TRASH_RETENTION_DAYS)

@Singleton
class InMemoryTrashVaultRepository @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
    private val sessionSceneDao: SessionSceneDao,
) : TrashVaultRepository {
    private val records = MutableStateFlow<List<TrashRecord>>(emptyList())

    override fun observeItems(): Flow<List<TrashItem>> = records.asStateFlow().map { entries ->
        entries.map(TrashRecord::item)
    }

    override suspend fun trashCampaign(campaignId: Long) {
        val campaign = campaignRepository.observeCampaign(campaignId).first() ?: return
        val sessions = sessionRepository.observeSessions(campaignId).first()
        val sessionSnapshots = mutableListOf<SessionSnapshot>()
        for (session in sessions) {
            sessionSnapshots += SessionSnapshot(
                session = session,
                linkedSceneIds = sessionSceneDao.getSceneIdsBySession(session.id),
            )
        }
        addRecord(
            CampaignTrashRecord(
                item = campaign.toTrashItem(TrashItemType.CAMPAIGN),
                campaign = campaign,
                sessions = sessionSnapshots,
            ),
        )
    }

    override suspend fun trashSession(sessionId: Long) {
        val session = sessionRepository.observeSession(sessionId).first() ?: return
        addRecord(
            SessionTrashRecord(
                item = session.toTrashItem(TrashItemType.SESSION),
                session = session,
                linkedSceneIds = sessionSceneDao.getSceneIdsBySession(sessionId),
            ),
        )
    }

    override suspend fun trashScene(sceneId: Long) {
        val scene = sceneRepository.observeScene(sceneId).first() ?: return
        addRecord(
            SceneTrashRecord(
                item = scene.toTrashItem(TrashItemType.SCENE),
                scene = scene,
                linkedSessionIds = sessionSceneDao.getSessionIdsByScene(sceneId),
                soundscapes = sceneRepository.observeSceneSoundscapes(sceneId).first(),
                fx = sceneRepository.observeSceneFx(sceneId).first(),
            ),
        )
    }

    override suspend fun trashSoundscapeCategory(categoryId: Long) {
        val category = soundscapeRepository.observeCategory(categoryId).first() ?: return
        addRecord(
            SoundscapeTrashRecord(
                item = category.toTrashItem(),
                category = category,
            ),
        )
    }

    override suspend fun trashFxTrack(trackId: Long) {
        val track = fxRepository.observeTracks().first().firstOrNull { it.id == trackId } ?: return
        addRecord(
            FxTrashRecord(
                item = track.toTrashItem(),
                track = track,
            ),
        )
    }

    override suspend fun restore(itemKey: String) {
        val record = records.value.firstOrNull { it.item.key == itemKey } ?: return
        when (record) {
            is CampaignTrashRecord -> restoreCampaign(record)
            is SessionTrashRecord -> restoreSession(record)
            is SceneTrashRecord -> restoreScene(record)
            is SoundscapeTrashRecord -> restoreSoundscape(record)
            is FxTrashRecord -> restoreFx(record)
        }
        removeRecord(itemKey)
    }

    override suspend fun permanentlyDelete(itemKey: String) {
        removeRecord(itemKey)
    }

    override suspend fun emptyVault() {
        records.value = emptyList()
    }

    override suspend fun purgeExpired(nowMillis: Long) {
        records.update { existing ->
            existing.filter { record ->
                nowMillis - record.item.deletedAtMillis < TRASH_RETENTION_MILLIS
            }
        }
    }

    override fun reset() {
        records.value = emptyList()
    }

    private suspend fun restoreCampaign(record: CampaignTrashRecord) {
        campaignRepository.upsertCampaign(record.campaign)
        val activeSceneIds = sceneRepository.observeScenes().first().map(Scene::id).toSet()
        for (snapshot in record.sessions) {
            sessionRepository.upsertSession(snapshot.session)
            val restorableSceneIds = snapshot.linkedSceneIds.filter(activeSceneIds::contains)
            if (restorableSceneIds.isNotEmpty()) {
                sessionRepository.linkScenes(snapshot.session.id, restorableSceneIds)
            }
        }
    }

    private suspend fun restoreSession(record: SessionTrashRecord) {
        sessionRepository.upsertSession(record.session)
        val activeSceneIds = sceneRepository.observeScenes().first().map(Scene::id).toSet()
        val restorableSceneIds = record.linkedSceneIds.filter(activeSceneIds::contains)
        if (restorableSceneIds.isNotEmpty()) {
            sessionRepository.linkScenes(record.session.id, restorableSceneIds)
        }
    }

    private suspend fun restoreScene(record: SceneTrashRecord) {
        sceneRepository.upsertScene(record.scene)
        sceneRepository.updateSceneAtmosphereVolume(record.scene.id, record.scene.atmosphereVolumePercent)
        sceneRepository.updateSceneSoundboardVolume(record.scene.id, record.scene.soundboardVolumePercent)

        val orderedSoundscapes = record.soundscapes.sortedBy(SceneSoundscape::displayOrder)
        for (soundscape in orderedSoundscapes) {
            sceneRepository.addSoundscapeCategory(record.scene.id, soundscape.categoryName)
            sceneRepository.updateSoundscapeMix(record.scene.id, soundscape.categoryId, soundscape.mixVolumePercent)
            sceneRepository.updateSoundscapeIntensity(record.scene.id, soundscape.categoryId, soundscape.intensityLevel)
        }
        if (orderedSoundscapes.isNotEmpty()) {
            sceneRepository.reorderSoundscapes(record.scene.id, orderedSoundscapes.map(SceneSoundscape::categoryId))
        }

        val orderedFx = record.fx.sortedBy(SceneFx::displayOrder)
        for (fx in orderedFx) {
            sceneRepository.addSoundboardEffect(record.scene.id, fx.fxTrackId)
        }
        if (orderedFx.isNotEmpty()) {
            sceneRepository.reorderSoundboardEffects(record.scene.id, orderedFx.map(SceneFx::fxTrackId))
        }

        val existingSessionIds = mutableSetOf<Long>()
        for (campaign in campaignRepository.observeCampaigns().first()) {
            existingSessionIds += sessionRepository.observeSessions(campaign.id).first().map(Session::id)
        }
        for (sessionId in record.linkedSessionIds.filter(existingSessionIds::contains)) {
            sessionRepository.linkScenes(sessionId, listOf(record.scene.id))
        }
    }

    private suspend fun restoreSoundscape(record: SoundscapeTrashRecord) {
        soundscapeRepository.updateCategory(record.category)
        soundscapeRepository.replaceTracks(record.category.id, record.category.tracks)
    }

    private suspend fun restoreFx(record: FxTrashRecord) {
        fxRepository.upsertTrack(record.track)
    }

    private fun addRecord(record: TrashRecord) {
        records.update { existing ->
            (existing.filterNot { item ->
                item.item.name == record.item.name && item.item.type == record.item.type
            } + record).sortedByDescending { it.item.deletedAtMillis }
        }
    }

    private fun removeRecord(itemKey: String) {
        records.update { existing -> existing.filterNot { it.item.key == itemKey } }
    }
}

private sealed interface TrashRecord {
    val item: TrashItem
}

private data class CampaignTrashRecord(
    override val item: TrashItem,
    val campaign: Campaign,
    val sessions: List<SessionSnapshot>,
) : TrashRecord

private data class SessionTrashRecord(
    override val item: TrashItem,
    val session: Session,
    val linkedSceneIds: List<Long>,
) : TrashRecord

private data class SceneTrashRecord(
    override val item: TrashItem,
    val scene: Scene,
    val linkedSessionIds: List<Long>,
    val soundscapes: List<SceneSoundscape>,
    val fx: List<SceneFx>,
) : TrashRecord

private data class SoundscapeTrashRecord(
    override val item: TrashItem,
    val category: SoundscapeCategory,
) : TrashRecord

private data class FxTrashRecord(
    override val item: TrashItem,
    val track: FxTrack,
) : TrashRecord

private data class SessionSnapshot(
    val session: Session,
    val linkedSceneIds: List<Long>,
)

private fun Campaign.toTrashItem(type: TrashItemType): TrashItem = TrashItem(
    key = "${type.name.lowercase()}:$id",
    name = name,
    type = type,
    deletedAtMillis = System.currentTimeMillis(),
)

private fun Session.toTrashItem(type: TrashItemType): TrashItem = TrashItem(
    key = "${type.name.lowercase()}:$id",
    name = name,
    type = type,
    deletedAtMillis = System.currentTimeMillis(),
)

private fun Scene.toTrashItem(type: TrashItemType): TrashItem = TrashItem(
    key = "${type.name.lowercase()}:$id",
    name = name,
    type = type,
    deletedAtMillis = System.currentTimeMillis(),
)

private fun SoundscapeCategory.toTrashItem(): TrashItem = TrashItem(
    key = "${TrashItemType.SOUNDSCAPE.name.lowercase()}:$id",
    name = name,
    type = TrashItemType.SOUNDSCAPE,
    deletedAtMillis = System.currentTimeMillis(),
)

private fun FxTrack.toTrashItem(): TrashItem = TrashItem(
    key = "${TrashItemType.FX.name.lowercase()}:$id",
    name = name,
    type = TrashItemType.FX,
    deletedAtMillis = System.currentTimeMillis(),
)
