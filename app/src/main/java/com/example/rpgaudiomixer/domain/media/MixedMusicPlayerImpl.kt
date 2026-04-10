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
    private var previewPlayer: TrackPlayer? = null

    override fun playSingleSound(soundId: String) {
        val soundFilePath = trackRepository.getTrackFilePath(soundId)
        val trackPlayer = trackFactory.createOneTimeTrackPlayer(soundFilePath)
        trackPlayer.play()
    }

    override fun playLoopingSound(categoryId: String) {
        TODO("Not yet implemented")
    }

    override fun previewSound(soundId: String) {
        previewPlayer?.stop()
        previewPlayer?.release()
        val soundFilePath = trackRepository.getTrackFilePath(soundId)
        previewPlayer = trackFactory.createOneTimeTrackPlayer(soundFilePath).also { player ->
            player.play()
        }
    }

    override fun pausePreview() {
        previewPlayer?.pause()
    }

    override fun stopPreview() {
        previewPlayer?.stop()
        previewPlayer?.release()
        previewPlayer = null
    }
}
