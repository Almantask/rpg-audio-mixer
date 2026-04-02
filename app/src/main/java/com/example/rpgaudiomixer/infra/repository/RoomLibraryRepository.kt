package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.Track
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.infra.db.dao.LibraryDao
import com.example.rpgaudiomixer.infra.db.toDomain
import com.example.rpgaudiomixer.infra.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLibraryRepository @Inject constructor(
    private val dao: LibraryDao,
) : LibraryRepository {

    override fun getAllCategories(): Flow<List<SoundscapeCategory>> =
        dao.getAllCategories().map { entities ->
            entities.map { entity ->
                val tracks = dao.getTracksForCategory(entity.id).map { it.toDomain() }
                val byIntensity = tracks.groupBy { it.intensityLevel }
                entity.toDomain(byIntensity)
            }
        }

    override suspend fun getCategoryById(id: Long): SoundscapeCategory? {
        val entity = dao.getCategoryById(id) ?: return null
        val tracks = dao.getTracksForCategory(id).map { it.toDomain() }
        val byIntensity = tracks.groupBy { it.intensityLevel }
        return entity.toDomain(byIntensity)
    }

    override suspend fun upsertCategory(category: SoundscapeCategory): Long =
        dao.upsertCategory(category.toEntity())

    override suspend fun deleteCategory(id: Long) =
        dao.deleteCategory(id)

    override suspend fun getTracksForCategory(categoryId: Long): List<Track> =
        dao.getTracksForCategory(categoryId).map { it.toDomain() }

    override suspend fun upsertTrack(track: Track): Long =
        dao.upsertTrack(track.toEntity())

    override suspend fun deleteTrack(id: Long) =
        dao.deleteTrack(id)

    override suspend fun updateTrackMixVolume(trackId: Long, volume: Float) =
        dao.updateTrackMixVolume(trackId, volume)

    override fun getAllFXTracks(): Flow<List<FXTrack>> =
        dao.getAllFXTracks().map { list -> list.map { it.toDomain() } }

    override suspend fun getFXTrackById(id: Long): FXTrack? =
        dao.getFXTrackById(id)?.toDomain()

    override suspend fun upsertFXTrack(fxTrack: FXTrack): Long =
        dao.upsertFXTrack(fxTrack.toEntity())

    override suspend fun deleteFXTrack(id: Long) =
        dao.deleteFXTrack(id)

    override suspend fun updateFXTrack(fxTrack: FXTrack) {
        dao.upsertFXTrack(fxTrack.toEntity())
    }

    override suspend fun getMostPlayedLoopingTrack(): Track? =
        dao.getMostPlayedTrack()?.toDomain()

    override suspend fun getMostPlayedFXTrack(): FXTrack? =
        dao.getMostPlayedFXTrack()?.toDomain()

    override suspend fun incrementTrackPlayCount(trackId: Long) =
        dao.incrementTrackPlayCount(trackId)

    override suspend fun incrementFXPlayCount(fxTrackId: Long) =
        dao.incrementFXPlayCount(fxTrackId)
}
