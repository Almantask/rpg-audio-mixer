package com.example.rpgaudiomixer.infra.media

import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoTrackFactory: TrackFactory{
    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        return ExoLoopableTrackPlayer(track)
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        return ExoOneTimeTrackPlayer(track)
    }
}