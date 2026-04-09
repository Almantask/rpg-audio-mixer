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
        trackPlayer.play()
    }

    override fun playLoopingSound(categoryId: String) {
        // Placeholder until the dedicated looping audio engine lands in a later iteration.
    }

    override fun previewTrack(trackPath: String) {
        val trackPlayer = trackFactory.createOneTimeTrackPlayer(trackPath)
        trackPlayer.play()
    }

    override fun pausePreview() {
        // Placeholder until preview playback keeps an owned player instance.
    }

    override fun stopPreview() {
        // Placeholder until preview playback keeps an owned player instance.
    }
}
