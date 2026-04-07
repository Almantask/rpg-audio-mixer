package com.example.rpgaudiomixer.infra.media

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoLoopableTrackPlayer(
    private val exoPlayer: ExoPlayer,
    private val mediaItem: MediaItem
) : TrackPlayer {

    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    override fun play() {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun resume() {
        exoPlayer.play()
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        exoPlayer.release()
    }
}
