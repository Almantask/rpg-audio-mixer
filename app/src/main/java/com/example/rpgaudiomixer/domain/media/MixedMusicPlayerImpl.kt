package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.storage.TrackRepository

/**
 * Placeholder Android implementation.
 *
 * A real implementation would use SoundPool/MediaPlayer/ExoPlayer depending on requirements.
 */
class MixedMusicPlayerImpl(
    private val trackFactory: TrackFactory,
    val trackRepository: TrackRepository,
) : MixedMusicPlayer {

    override fun playSingleSound(soundId: String) {
        val soundFilePath = trackRepository.getTrackFilePath(soundId)
        val trackPlayer = trackFactory.createOneTimeTrackPlayer(soundFilePath)
        trackPlayer.playTrack()
    }

    override fun playLoopingSound(categoryId: String) {
        val soundFilePath = trackRepository.getTrackFilePath(categoryId)
        val trackPlayer = trackFactory.createLoopableTrackPlayer(soundFilePath)
        trackPlayer.playTrack()
    }
}