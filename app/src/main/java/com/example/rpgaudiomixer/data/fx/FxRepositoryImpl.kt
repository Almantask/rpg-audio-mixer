package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FxRepositoryImpl @Inject constructor(
    private val fxTrackDao: FxTrackDao,
    private val fxAudioImporter: FxAudioImporter,
) : FxRepository {

    override fun observeFxTracks(): Flow<List<FxTrack>> =
        fxTrackDao.observeAll().map { tracks -> tracks.map(FxTrackEntity::toDomain) }

    override fun searchFxTracks(query: String): Flow<List<FxTrack>> =
        fxTrackDao.search(query.trim()).map { tracks -> tracks.map(FxTrackEntity::toDomain) }

    override fun observeHasDemoFxTracks(): Flow<Boolean> = fxTrackDao.observeDemoContentAvailable()

    override suspend fun importFxTrack(sourceUri: String): FxTrack {
        val importedTrack = fxAudioImporter.importAudio(sourceUri)
        val trackEntity = FxTrackEntity(
            name = importedTrack.displayName,
            filePath = importedTrack.storedPath,
            tags = "",
            durationMs = importedTrack.durationMs,
            playCount = 0,
            isDemo = false,
            isDeleted = false,
        )
        val trackId = fxTrackDao.upsert(trackEntity)
        return trackEntity.copy(id = trackId).toDomain()
    }

    override suspend fun updateFxTrack(track: FxTrack) {
        fxTrackDao.upsert(track.toEntity())
    }

    override suspend fun softDeleteFxTrack(trackId: Long) {
        fxTrackDao.softDelete(trackId)
    }

    override suspend fun seedDemoFxTracks() {
        val demoTracks = List(100) { index ->
            val tag = demoTagFor(index)
            FxTrackEntity(
                name = "demo_fx_${index + 1}.mp3",
                filePath = "file:///demo/fx/demo_fx_${index + 1}.mp3",
                tags = tag,
                durationMs = 1500L + (index * 40L),
                playCount = 0,
                isDemo = true,
                isDeleted = false,
            )
        }
        fxTrackDao.upsertAll(demoTracks)
    }
}

private fun FxTrackEntity.toDomain(): FxTrack = FxTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.split(",").map(String::trim).filter(String::isNotBlank),
    durationMs = durationMs,
    playCount = playCount,
    isDemo = isDemo,
)

private fun FxTrack.toEntity(): FxTrackEntity = FxTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.joinToString(","),
    durationMs = durationMs,
    playCount = playCount,
    isDemo = isDemo,
    isDeleted = false,
)

private fun demoTagFor(index: Int): String = when (index % 5) {
    0 -> "Combat"
    1 -> "Weather"
    2 -> "Nature"
    3 -> "Magic"
    else -> "Creature"
}
