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
    private var currentPreviewPlayer: TrackPlayer? = null

    override fun playSingleSound(soundId: String) {
        val soundFilePath = trackRepository.getTrackFilePath(soundId)
        val trackPlayer = trackFactory.createOneTimeTrackPlayer(soundFilePath)
        trackPlayer.play()
    }

    override fun playLoopingSound(categoryId: String) {
        // Placeholder until the dedicated looping audio engine lands in a later iteration.
    }

    override fun previewTrack(trackPath: String) {
        currentPreviewPlayer?.stop()
        currentPreviewPlayer = trackFactory.createOneTimeTrackPlayer(trackPath).also { trackPlayer ->
            trackPlayer.play()
        }
    }

    override fun pausePreview() {
        currentPreviewPlayer?.pause()
    }

    override fun stopPreview() {
        currentPreviewPlayer?.stop()
        currentPreviewPlayer = null
    }
}
