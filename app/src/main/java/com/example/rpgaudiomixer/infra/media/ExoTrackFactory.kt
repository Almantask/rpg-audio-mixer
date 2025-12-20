package com.example.rpgaudiomixer.infra.media

import android.content.Context
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoTrackFactory(
    private val appContext: Context,
) : TrackFactory {

    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        return ExoLoopableTrackPlayer(track = track, appContext = appContext)
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        return ExoOneTimeTrackPlayer(track = track, appContext = appContext)
    }
}