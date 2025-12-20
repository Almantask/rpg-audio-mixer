package com.example.rpgaudiomixer.domain.media

interface TrackFactory {
    fun createLoopableTrackPlayer(track: String): TrackPlayer
    fun createOneTimeTrackPlayer(track: String): TrackPlayer
}