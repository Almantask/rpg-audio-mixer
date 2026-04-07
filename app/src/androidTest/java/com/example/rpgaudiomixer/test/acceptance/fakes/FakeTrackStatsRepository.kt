package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.TrackStats
import com.example.rpgaudiomixer.domain.model.TrackType
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository

class FakeTrackStatsRepository : TrackStatsRepository {

    private val trackStats = mutableListOf<TrackStats>()

    fun setTrackStats(vararg stats: TrackStats) {
        this.trackStats.clear()
        this.trackStats.addAll(stats)
    }

    fun clear() {
        trackStats.clear()
    }

    override suspend fun getMostPlayedTrack(type: TrackType): TrackStats? {
        return trackStats
            .filter { it.type == type }
            .maxByOrNull { it.playCount }
    }
}
