package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.TrackStats
import com.example.rpgaudiomixer.domain.model.TrackType

interface TrackStatsRepository {
    suspend fun getMostPlayedTrack(type: TrackType): TrackStats?
}
