package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FxRepositoryImpl @Inject constructor(
    private val fxTrackDao: FxTrackDao,
) : FxRepository {
    override fun observeTracks(): Flow<List<FxTrack>> = fxTrackDao.observeAll()
        .map { tracks -> tracks.map(FxTrackEntity::toDomain) }

    override fun observeMostPlayedTrack(): Flow<FxTrack?> = fxTrackDao.observeMostPlayed()
        .map { track -> track?.toDomain() }

    override fun searchTracks(query: String): Flow<List<FxTrack>> = fxTrackDao.search(query.trim())
        .map { tracks -> tracks.map(FxTrackEntity::toDomain) }

    override fun hasDemoTracks(): Flow<Boolean> = fxTrackDao.hasDemoTracks()

    override suspend fun upsertTrack(track: FxTrack): Long = fxTrackDao.upsert(track.toEntity())

    override suspend fun deleteTrack(trackId: Long) {
        fxTrackDao.deleteById(trackId)
    }

    override suspend fun incrementPlayCount(trackId: Long) {
        val track = fxTrackDao.getById(trackId) ?: return
        fxTrackDao.upsert(track.copy(playCount = track.playCount + 1))
    }

    override suspend fun downloadDemoTracks() {
        val demoTags = listOf("Combat", "Magic", "Nature", "Creature")
        repeat(100) { index ->
            val tag = demoTags[index % demoTags.size]
            upsertTrack(
                FxTrack(
                    name = "$tag Demo FX ${index + 1}",
                    filePath = "demo://fx/${index + 1}",
                    tags = listOf(tag),
                    durationMs = 3_000L + (index % 5) * 1_000L,
                ),
            )
        }
    }

    override suspend fun clearAll() {
        fxTrackDao.clearAll()
    }
}

private fun FxTrackEntity.toDomain(): FxTrack = FxTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = tagsCsv.toCsvList(),
    durationMs = durationMs,
    playCount = playCount,
)

private fun FxTrack.toEntity(): FxTrackEntity = FxTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tagsCsv = tags.toCsvString(),
    durationMs = durationMs,
    playCount = playCount,
)

private fun String.toCsvList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)

private fun List<String>.toCsvString(): String = joinToString(",")
