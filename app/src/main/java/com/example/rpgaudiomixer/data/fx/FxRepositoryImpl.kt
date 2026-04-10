package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.asDomain
import com.example.rpgaudiomixer.data.fx.local.asEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FeaturedFxTrack
import com.example.rpgaudiomixer.domain.model.FxTrack
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FxRepositoryImpl @Inject constructor(
    private val fxTrackDao: FxTrackDao,
) : FxRepository {

    override fun observeAll(): Flow<List<FxTrack>> {
        return fxTrackDao.observeAll().map { tracks ->
            tracks.map { track -> track.asDomain() }
        }
    }

    override fun observeLegendaryAction(): Flow<FeaturedFxTrack?> {
        return fxTrackDao.observeMostPlayed().map { track ->
            track?.let {
                FeaturedFxTrack(
                    trackName = it.name,
                    categoryName = it.tags.split(",")
                        .map { tag -> tag.trim() }
                        .firstOrNull { tag -> tag.isNotEmpty() }
                        ?: "FX Library",
                    playCount = it.playCount,
                )
            }
        }
    }

    override fun search(query: String): Flow<List<FxTrack>> {
        return fxTrackDao.search(query).map { tracks ->
            tracks.map { track -> track.asDomain() }
        }
    }

    override suspend fun upsert(track: FxTrack): Long {
        return fxTrackDao.upsert(track.asEntity())
    }

    override suspend fun delete(trackId: Long) {
        fxTrackDao.softDelete(
            trackId = trackId,
            deletedAt = System.currentTimeMillis(),
        )
    }
}
