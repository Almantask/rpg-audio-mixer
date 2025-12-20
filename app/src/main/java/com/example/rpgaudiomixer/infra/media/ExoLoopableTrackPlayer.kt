package com.example.rpgaudiomixer.infra.media

import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoLoopableTrackPlayer(
    private val track: String
): TrackPlayer {

    override fun play() {
        // check if file by that name exists
        // if not, throw TrackNotFoundException
        // if yes, play it

        TODO("Not yet implemented")
    }
}