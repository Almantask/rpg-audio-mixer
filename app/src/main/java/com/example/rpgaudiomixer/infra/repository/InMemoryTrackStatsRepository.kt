package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.TrackStats
import com.example.rpgaudiomixer.domain.model.TrackType
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository

class InMemoryTrackStatsRepository : TrackStatsRepository {
    override suspend fun getMostPlayedTrack(type: TrackType): TrackStats? = null
}
