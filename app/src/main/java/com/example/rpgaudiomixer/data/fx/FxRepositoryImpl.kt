package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.infra.media.AudioMetadataReader
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FxRepositoryImpl @Inject constructor(
    private val trackDao: FxTrackDao,
    private val audioMetadataReader: AudioMetadataReader,
) : FxRepository {

    override fun observeTracks(): Flow<List<FxTrack>> {
        return trackDao.observeAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeMostPlayedTrack(): Flow<FxTrack?> {
        return trackDao.observeMostPlayedTrack().map { entity -> entity?.toDomainModel() }
    }

    override suspend fun importTrack(name: String, filePath: String): Result<Long> {
        return audioMetadataReader.readDurationMillis(filePath).mapCatching { durationMs ->
            trackDao.insert(
                FxTrackEntity(
                    name = name.trim(),
                    filePath = filePath,
                    tags = "",
                    durationMs = durationMs,
                    playCount = 0,
                    isDemoContent = false,
                ),
            )
        }
    }

    override suspend fun installDemoTracks() {
        if (trackDao.demoTrackCount() > 0) {
            return
        }

        trackDao.insertAll(
            List(100) { index ->
                FxTrackEntity(
                    name = "Demo FX ${index + 1}",
                    filePath = "demo://fx/${index + 1}",
                    tags = when {
                        index % 3 == 0 -> "Combat"
                        index % 3 == 1 -> "Nature"
                        else -> "Creature"
                    },
                    durationMs = 2_000L + (index % 5) * 500L,
                    playCount = 0,
                    isDemoContent = true,
                )
            },
        )
    }

    override suspend fun updateTrack(track: FxTrack) {
        trackDao.insert(
            FxTrackEntity(
                id = track.id,
                name = track.name.trim(),
                filePath = track.filePath,
                tags = track.tags.joinToString(","),
                durationMs = track.durationMs,
                playCount = track.playCount,
                isDemoContent = track.isDemoContent,
            ),
        )
    }

    override suspend fun deleteTrack(trackId: Long) {
        trackDao.deleteById(trackId)
    }
}

private fun FxTrackEntity.toDomainModel(): FxTrack {
    return FxTrack(
        id = id,
        name = name,
        filePath = filePath,
        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        durationMs = durationMs,
        playCount = playCount,
        isDemoContent = isDemoContent,
    )
}
